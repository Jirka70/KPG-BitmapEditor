# Bitmap editor - Jiri Tresohlavy

# Tasks
- create bitmap editor able to make advanced operations with bitmap images (max. 7 pts.) + doc
- make whole image blurred (1 pt.)
- make local blur - on mouse click (2 pts.)
- make magic wand - tool showing all continous similar pixels (2 pts.)
- make cloning stamp - selects part of image and then pastes it to the another part of image (2 pts.)

# Launch
Project is made in JavaFX framework. To launch project, just pull it to your favorite Java IDE and run `Launcher.java` class.

# Application
- on the top left of window is settled button with `Load` title. This button will load an image you want.
- on the right, there are situtated all the implemented tools (blur, magic wand, cloning stamp)
- on the left, you can find enormous blank space, when app is launched. This is the space, where loaded photo will be placed.
- on the right, there is status bar, which (hopefully) will guide you how to use the tools in the right bar. (All of that is descripted in this `readme` properly)

# Loading image
- image is loaded by clicking top left button `Load`
- if image is chosen, image will be displayed on the left of app. The data of image (each pixel as 32-bit integer) is stored in the 2D array (matrix). Each rgb-value is obtained thanks to bit shifting and bitwise operator `&`.

# Blurring
- app is able to blur whole image or just a part of image specified by mouse click
- in the right bar, there are in the section `Blur` two buttons: `Blur whole image!` and `Blur locally`
- `Blur whole image!` button will blur whole image with intensity specified in the slider above. (Max is 10 pixels and min is 1 pixel)
- `Blur locally` button will blur just a part of image (the part, where user clicks). When clicked, text is changed to `Exit mode`. When clicked this button with `Exit mode` text, you cannot blur image locally anymore. You need to press this button again. The intensity of local blur is hardcoded to **10 pixels**.
- blurring works really simple. It will simply calculate the average value of surrounding pixels. Blurring method receives parameter `blurIntensity` which stands for, how many surrounding pixels will be used to calculate an average value of pixel.
- E. G. if value is set to 8 -> all 4 pixels to the left, all 4 pixels to the right, all 4 pixels to the up and all 4 pixels downwards will be used for calculating average value of pixel.
- if surrounding pixel is out of range an image, than those pixel value app does not count.

  


<p align="center">
  <img width="490" alt="Bildschirmfoto 2024-04-04 um 12 16 07 AM" src="https://github.com/Jirka70/KPG-BitmapEditor/assets/120426468/71a77ff8-6ffa-4438-81e9-b2f56ae5f6ba">
  <br>
  <b>Blurred whole image</b>
  <br>
  <br>
  <img width="358" alt="Bildschirmfoto 2024-04-04 um 12 17 11 AM" src="https://github.com/Jirka70/KPG-BitmapEditor/assets/120426468/9851d670-1d6a-4e58-9fa4-f3fd8131032c">
  <br>
  <b>Blurred only a part of image (eyes)</b>
</p>

# Magic wand
- when clicking `Use Magic Wand` button, user is able to select all similar continuous pixels to the clicked one.
- algorithm is based on BFS, when all the similar neighbours (pixels) are added to the set of pixels, which is lately displayed straight on the image.
- if magic wand is applied more times, the marked set of pixels will stay in the image. 
- Similarity of two pixels is determined as:
  - $`\sqrt[4]{(a1 - a2)^4 + (r1 - r2)^4 + (g1 - g2)^4 + (b1 - b2)^4} < TOLERANCE`$
  - where `a1` and `a2` stands for alpha value of first comparing pixel and for second comparing pixel
  - where `r1` and `r2` stands for red value of first comparing pixel and for second comparing pixel
  - where `g1` and `g2` stands for green value of first comparing pixel and for second comparing pixel
  - where `b1` and `b2` stands for blue value of first comparing pixel and for second comparing pixel
  - where `TOLERANCE` is set to **15**
 
<p align="center">
  <img width="495" alt="Bildschirmfoto 2024-04-04 um 12 25 50 AM" src="https://github.com/Jirka70/KPG-BitmapEditor/assets/120426468/fe06e559-4aee-46d6-8812-3776e0e51349">
  <br>
  <b>Used magic wand on the white background of image</b>
</p>

# Cloning stamp
- when clicking `Use Cloning stamp` button, user is able to select and then paste selected part of image to the different place of image.
- firstly just select the part, you would like to copy:
<p align="center">
  <img width="484" alt="Bildschirmfoto 2024-04-04 um 12 31 04 AM" src="https://github.com/Jirka70/KPG-BitmapEditor/assets/120426468/70e6186b-82c7-4558-a448-5e9cadaefb41">
  <br>
  <b>Selected part of image (selected part is within the black rectangle)</b>
</p>
- when mouse is released, the content within the rectangle was copied and then you just need to click to the part of image, where you want to paste that content.
<p align="center">
    <img width="491" alt="Bildschirmfoto 2024-04-04 um 12 33 01 AM" src="https://github.com/Jirka70/KPG-BitmapEditor/assets/120426468/339ee15a-f583-4904-baa2-386772bbd889">
  <br>
  <b>Pasted selected content</b>
</p>

If you really know how to use it, sometimes, those images may look funny
<p align="center">
  <img width="369" alt="Bildschirmfoto 2024-04-04 um 12 35 02 AM" src="https://github.com/Jirka70/KPG-BitmapEditor/assets/120426468/8b15fb22-0054-46b5-884b-7ece0f184258">
  <br>
  <b>Example of funny usage of cloning stamp</b>
</p>

# Conclusion
App is equipped with few interesting tools (magic wand, bluring tool, cloning stamp). There is also introduced status bar, which can be useful for beginner users,
who do not know, how to use this app. Lastly, the code is not written well. Some methods are coded really in awful way. But all the tools fulfills their activity properly.

