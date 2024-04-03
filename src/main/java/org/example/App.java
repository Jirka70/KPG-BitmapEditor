package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class App extends Application {
    private static final int BLUR_MAX = 10;
    private static final int BLUR_MIN = 1;
    private static final double WIDTH = 800;
    private static final double HEIGHT = 600;
    private final ImageView imageView = new ImageView();
    private final BorderPane root = new BorderPane();
    private final Slider slider = new Slider();
    private int[][] imageMatrix = null;
    private final HBox statusBar = createStatusBar();
    private final StatusListener statusListener = createStatusListener();
    private double beginX = -1;
    private double beginY = -1;
    private Rectangle selectedPixelRectangle = null;
    private final Button stampButton = new Button("Use Cloning stamp");

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(WIDTH);
        primaryStage.setMinHeight(HEIGHT);
        primaryStage.setTitle("KPG5");
        primaryStage.show();

        displayComponents();
    }

    private void displayComponents() {
        VBox rightBox = createRightBox();
        HBox topBox = createTopMenuBox();
        Pane centerBox = createCenterBox();
        root.setRight(rightBox);
        root.setTop(topBox);
        root.setCenter(centerBox);
        root.setBottom(statusBar);
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(new Label());
        statusBar.setPadding(new Insets(5));
        return statusBar;
    }

    private VBox createRightBox() {
        final int MAX_WIDTH = 400;
        VBox rightBox = new VBox(20);
        rightBox.setPadding(new Insets(10));
        slider.valueProperty().addListener(e -> slider.setValue(Math.round(slider.getValue())));
        slider.setMax(BLUR_MAX);
        slider.setMin(BLUR_MIN);
        rightBox.prefWidthProperty().bind(Bindings.createDoubleBinding(
                () -> Math.min(MAX_WIDTH, root.widthProperty().get() / 3)));
        rightBox.setAlignment(Pos.TOP_CENTER);
        Label label = new Label("Blur strength");
        Label sliderDisplayLabel = new Label();
        sliderDisplayLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("%.0f pixels", slider.getValue()), slider.valueProperty()));
        Button blurButton = new Button("Blur whole image!");
        Button blurLocallyButton = new Button("Blur locally");
        HBox blurButtonBox = new HBox(10);
        blurButtonBox.setAlignment(Pos.CENTER);
        blurButtonBox.getChildren().addAll(blurButton, blurLocallyButton);

        blurButton.setOnAction(e -> blurImage());
        blurLocallyButton.setOnAction(e -> enterBlurMode(blurLocallyButton));
        VBox magicWandBox = createMagicWandBox();
        VBox stampBox = createCloneStampBox();
        rightBox.getChildren().addAll(label, slider, sliderDisplayLabel, blurButtonBox, magicWandBox, stampBox);
        return rightBox;
    }

    private VBox createMagicWandBox() {
        VBox magicWandBox = new VBox(10);
        Label magicWandTitle = new Label("Magic wand");
        Button magicWandButton = new Button("Use magic wand");
        magicWandButton.setOnAction(e -> enterMagicWandMode(magicWandButton));
        magicWandBox.getChildren().addAll(magicWandTitle, magicWandButton);
        return magicWandBox;
    }

    private VBox createCloneStampBox() {
        VBox stampBox = new VBox(10);
        Label stampBoxTitle = new Label("Cloning stamp");
        stampButton.setOnAction(e -> enterCloneStampMode(stampButton));
        stampBox.getChildren().addAll(stampBoxTitle, stampButton);
        return stampBox;
    }

    private void enterCloneStampMode(Button cloningStampButton) {
        cloningStampButton.setText("Exit clone stamp mode");
        statusListener.message("You entered clone stamp mode. Now you can select the area you want to clone");
        cloningStampButton.setOnAction(e -> exitCloneStampMode());
        imageView.setOnMouseDragged(this::selectAreaToClone);
        imageView.setOnMouseReleased(e -> selectPlaceToCopySelectedSubImage());
    }

    private void selectPlaceToCopySelectedSubImage() {
        if (selectedPixelRectangle == null) {
            return;
        }
        int startX = calculateXPositionOfImage(selectedPixelRectangle.getX());
        int startY = calculateYPositionOfImage(selectedPixelRectangle.getY());
        int endX = calculateXPositionOfImage(selectedPixelRectangle.getX() + selectedPixelRectangle.getWidth());
        int endY = calculateYPositionOfImage(selectedPixelRectangle.getY() + selectedPixelRectangle.getHeight());

        CloneStamp cloneStamp = new CloneStamp(imageMatrix);
        cloneStamp.cloneSelectedArea(startX, startY, endX, endY);

        imageView.setOnMouseClicked(e -> pasteCopiedSubImage(e, cloneStamp));
        imageView.setOnMouseReleased(null);
        imageView.setOnMouseDragged(null);
        statusListener.message("Now select place, where you want to paste selected sub image");
    }

    private void pasteCopiedSubImage(MouseEvent e, CloneStamp cloneStamp) {
        int x = calculateXPositionOfImage(e.getX());
        int y = calculateYPositionOfImage(e.getY());
        cloneStamp.pasteSelectedArea(x, y);
        Pane center = (Pane) root.getCenter();
        center.getChildren().removeIf(el -> el instanceof Rectangle);
        imageView.setOnMouseClicked(null);
        displayImageMatrix();
        beginX = -1;
        beginY = -1;
        selectedPixelRectangle = null;
        exitCloneStampMode();
        statusListener.success("Sub image has been placed successfully");
    }

    private void exitCloneStampMode() {
        stampButton.setText("Use Cloning Stamp");
        statusListener.message("You exited clone stamp mode");
        stampButton.setOnAction(e -> enterCloneStampMode(stampButton));
        imageView.setOnMouseClicked(null);
        imageView.setOnMouseDragged(null);
        imageView.setOnMouseReleased(null);
    }

    private void selectAreaToClone(MouseEvent mouseEvent) {
        Pane center = (Pane) root.getCenter();
        beginX = beginX == -1 ? mouseEvent.getX() : beginX;
        beginY = beginY == -1 ? mouseEvent.getY() : beginY;

        double endX = mouseEvent.getX();
        double endY = mouseEvent.getY();

        selectedPixelRectangle = selectedPixelRectangle == null ? new Rectangle(beginX, beginY, Math.abs(endX - beginX), Math.abs(endY - beginY)) : selectedPixelRectangle;
        selectedPixelRectangle.setFill(Color.TRANSPARENT);
        selectedPixelRectangle.setStroke(Color.BLACK);

        setRectangle(selectedPixelRectangle, beginX, endX, beginY, endY);
        if (!center.getChildren().contains(selectedPixelRectangle)) {
            center.getChildren().add(selectedPixelRectangle);
        }

    }

    private void setRectangle(Rectangle selectedPixelRectangle, double beginX, double endX, double beginY, double endY) {
        double x = Math.min(endX, beginX);
        double y = Math.min(endY, beginY);
        double width = Math.abs(endX - beginX);
        double height = Math.abs(endY - beginY);

        if (endX < beginX) {
            x = endX;
        }
        if (endY < beginY) {
            y = endY;
        }

        selectedPixelRectangle.setX(x);
        selectedPixelRectangle.setY(y);
        selectedPixelRectangle.setWidth(width);
        selectedPixelRectangle.setHeight(height);
    }

    private void enterMagicWandMode(Button magicWandButton) {
        magicWandButton.setText("Exit magic wand mode");
        statusListener.message("You entered magic wand mode. Now you can select the whole area of photo with similar pixels");
        magicWandButton.setOnAction(e -> exitMagicWandMode(magicWandButton));
        imageView.setOnMouseClicked(this::useMagicWand);
    }

    private void useMagicWand(MouseEvent e) {
        Thread thread = new Thread(() -> {
            MagicWand magicWand = new MagicWand(imageMatrix);
            double clickedX = e.getX();
            double clickedY = e.getY();
            statusListener.message("Applying magic wand...");

            int rightX = calculateXPositionOfImage(clickedX);
            int rightY = calculateYPositionOfImage(clickedY);
            Set<MagicWand.Pixel> selectedPixels = magicWand.findAllSimilarPixels(rightX, rightY);
            displaySelectedPixels(selectedPixels);
            statusListener.success("Magic wand has been successfully applied!");
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void displaySelectedPixels(Set<MagicWand.Pixel> selectedPixels) {
        int value = (100 << 24) | (50 << 16) | (50 << 8) | 250;
        for (MagicWand.Pixel pixel : selectedPixels) {
            int pixelX = pixel.x;
            int pixelY = pixel.y;

            imageMatrix[pixelY][pixelX] = value;
        }

        displayImageMatrix();
    }

    private void exitMagicWandMode(Button magicWandButton) {
        statusListener.message("Magic wand mode was exited");
        imageView.setOnMouseClicked(null);
        magicWandButton.setText("Use Magic Wand");
        magicWandButton.setOnAction(e -> enterMagicWandMode(magicWandButton));
    }

    private void enterBlurMode(Button blurLocallyButton) {
        statusListener.message("You entered blurring mode. Now you can blur image locally by mouse clicking on image");
        imageView.setOnMouseClicked(e -> blurLocally(e.getX(), e.getY()));
        blurLocallyButton.setText("Exit mode");
        blurLocallyButton.setOnAction(e -> exitBlurMode(blurLocallyButton));
    }

    private void blurLocally(double x, double y) {
        ImageBlur imageBlur = new ImageBlur(imageMatrix);
        int rightXPos = calculateXPositionOfImage(x);
        int rightYPos = calculateYPositionOfImage(y);
        System.out.println(rightXPos + " " + rightYPos);
        imageBlur.blurImageLocally(rightXPos, rightYPos);
        displayImageMatrix();
    }


    private int calculateXPositionOfImage(double x) {
        int matrixWidth = imageMatrix[0].length;
        double imageWidth = imageView.getFitWidth();
        double multiplier = matrixWidth / imageWidth;
        return (int) (x * multiplier);
    }

    private int calculateYPositionOfImage(double y) {
        int matrixHeight = imageMatrix.length;
        double imageHeight = imageView.getFitHeight();
        double multiplier = matrixHeight / imageHeight;
        return (int) (y * multiplier);
    }

    private void exitBlurMode(Button blurLocallyButton) {
        statusListener.message("Exited blurring mode");
        imageView.setOnMouseClicked(null);
        blurLocallyButton.setText("Blur locally");
        blurLocallyButton.setOnAction(e -> enterBlurMode(blurLocallyButton));
    }

    private void blurImage() {
        Thread thread = new Thread(() -> {
            ImageBlur imageBlur = new ImageBlur(imageMatrix);
            statusListener.message("Blurring whole image...");
            imageBlur.blurImage((int) slider.getValue()); // int conversion is ok, because the slider value step is 1
            displayImageMatrix();
            statusListener.success("Whole image has been successfully blurred!");
        });

        thread.setDaemon(true);
        thread.start();
    }

    private Pane createCenterBox() {
        return new Pane(imageView);
    }

    private HBox createTopMenuBox() {
        HBox topMenuBox = new HBox();
        Button loadBtn = new Button("Load");
        loadBtn.setOnAction(e -> loadFile());
        topMenuBox.getChildren().add(loadBtn);
        return topMenuBox;
    }


    private StatusListener createStatusListener() {
        return new StatusListener() {
            @Override
            public void message(String message) {
                Platform.runLater(() -> displayMessageToStatusBar(message, "transparent"));
            }

            @Override
            public void error(String message) {
                Platform.runLater(() -> displayMessageToStatusBar(message, "rgb(200,120,120)"));
            }

            @Override
            public void success(String message) {
                Platform.runLater(() -> displayMessageToStatusBar(message, "rgb(94,170,94)"));
            }
        };
    }

    private void displayMessageToStatusBar(String message, String backgroundColorRGB) {
        Label statusLabel = (Label) statusBar.getChildren().get(0);
        statusBar.setStyle("-fx-background-color: " + backgroundColorRGB);
        statusLabel.setText(message);
    }

    private void loadFile() {
        FileChooser chooser = new FileChooser();
        Window homeWindow = root.getScene().getWindow();
        chooser.setTitle("Choose image");

        File chosenFile = chooser.showOpenDialog(homeWindow);

        if (chosenFile != null) {
            try {
                statusListener.message("Loading file " + chosenFile);
                displayFile(chosenFile.toString());
                statusListener.success("Image " + chosenFile + " successfully loaded");
            } catch (Exception e) {
                statusListener.error("Loaded image cannot be converted to image");
            }
        } else {
            statusListener.message("Image was not loaded");
        }
    }

    private void displayFile(String file) throws IOException {
        ImageReader imageReader = new ImageReader(file);

        imageMatrix = imageReader.loadImage();
        displayImageMatrix();
        editImageViewSizeCorrectly();

    }

    private void displayImageMatrix() {
        ImageMaker imageMaker = new ImageMaker(imageMatrix);
        Image loadedImage = imageMaker.createImage();

        imageView.setImage(loadedImage);
    }

    private void editImageViewSizeCorrectly() {
        final int OFFSET = 50;
        final int PREF_WIDTH = 500;
        Image image = imageView.getImage();
        double width = image.getWidth();
        double height = image.getHeight();
        double divisor = width / PREF_WIDTH;
        double fitHeight = height / divisor;
        if (fitHeight > HEIGHT - OFFSET) {
            divisor = height / (HEIGHT - OFFSET);
            width /= divisor;
            imageView.setFitWidth(width);
            imageView.setFitHeight(HEIGHT - OFFSET);
        } else {
            imageView.setFitHeight(height / divisor);
            imageView.setFitWidth(PREF_WIDTH);
        }
    }
}
