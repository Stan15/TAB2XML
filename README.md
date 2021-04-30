# TAB2XML

## Demo
Before we get down to business, here is a quick video demo of what our applicaiton can do.<br/>
[![a demo of converting tablature text file into a playable MusicXML format](http://img.youtube.com/vi/QMvCKka3bOA/0.jpg)](http://www.youtube.com/watch?v=QMvCKka3bOA "Tablature editor and Tab to MusicXML converter")<br/><br/>

## 1. About TAB2XML

1.1 Product Name and Intended Use

TAB2XML is a tool designed to convert musical tablature in a text format into MusicXML, a popular open-source file format used for exchanging digital sheet music.  TAB2XML is a java-based application that is currently actively supported, with more features being added every week. 
  
1.2 Features of Product

This product is able to convert tablature of following instruments: Guitar, Drum, Bass.

Note: Since the product has not been implemented fully, instruments that the product can convert are limited, as of now. Reference How to Use. <br/><br/>



## 2. System Requirements

|     |           |
|-----|-----------|
|Available Disk Space |	50 MB|
|RAM | 256 MB|
|Java version | Java 15|
|Operating System |	Windows, MacOS, any platform with gradle|. <br/><br/><br/>

<br/>

## 3. Installing TAB2XML using Gradle

TAB2XML is built as a Gradle project and thus should work on any IDE of your choice. 
However, we will only go over how to run the program using the IntelliJ and Eclipse IDE.

3.1	IntelliJ

1. We first need to clone the project from the online GitHub repository onto our local
device. To do that, first open IntelliJ. At the top right, select File > New > Project from 
Version Control. <br/>
![image](https://user-images.githubusercontent.com/76922658/111523021-32c74700-8731-11eb-9915-3987fdf4632a.png) <br/><br/>


2. This will direct you to a window which prompts you to enter a repository URL. Enter the
link https://github.com/Stan15/TAB2XML as the URL, and select a directory where the 
project should be saved. <br/>
![image](https://user-images.githubusercontent.com/76922658/111525193-b124e880-8733-11eb-9a1d-048f1a0a584b.png) <br/><br/>


3. Once the project opens, click the Gradle toolbar on the right side of your screen. <br/>
![image](https://user-images.githubusercontent.com/76922658/111525232-bb46e700-8733-11eb-8767-fe31c30b2e6b.png) <br/><br/>


4. When the toolbar opens, click Tasks > build, and then right click “build” and the
 following dialog should pop up. Click “Run TAB2XML [build]”. <br/>
![image](https://user-images.githubusercontent.com/76922658/111502360-2c7aa000-871c-11eb-9d27-c49dedaf83a1.png) <br/>

5. Finally select Tasks > application, and then right click the run task and select Run 
‘TAB2XML [run]’. The program will now launch. <br/><br/>


3.2	Eclipse<br/>
Watch this short video on how to run the program using eclipse. The video is more up to date. The outdated instructions will be updated soon.
<br/>
[![instructions for running TAB2XML with Eclipse](http://img.youtube.com/vi/YSPMNJX8yFQ/0.jpg)](http://www.youtube.com/watch?v=YSPMNJX8yFQ "Eclipse instructions")
<br/><br/>
1. Like with IntelliJ, we want to clone the project from the GitHub repository. To do this, we select File > Import. <br/>
![image](https://user-images.githubusercontent.com/76922658/111523398-a0737300-8731-11eb-9d44-f9db034994fc.png) <br/><br/>
 

2. This will take you to a new window like the one below. Under the Git folder, click Projects from Git (with smart import). <br/>
![image](https://user-images.githubusercontent.com/76922658/111523439-a8cbae00-8731-11eb-9982-dc57fbd8345a.png) <br/><br/>


3. Next, click clone URI <br/>
![image](https://user-images.githubusercontent.com/76922658/111503482-2e912e80-871d-11eb-96d0-d31450891d02.png) <br/><br/>


4. This will take you to a window where you are prompted to input a URI. Paste in the link https://github.com/Stan15/TAB2XML and click proceed with the steps (clicking next) until you see the finish button. Click on that. <br/>
![image](https://user-images.githubusercontent.com/76922658/111523502-b5e89d00-8731-11eb-930f-ec7932f04a5c.png) <br/><br/>


5. Now we have imported the project onto our local device, we want import it again as a Gradle project. Repeat step 1. When you get to the view shown in step 2, instead of clicking projects from Git, click Existing Gradle project. Proceed till you come to this page. <br/>
![image](https://user-images.githubusercontent.com/76922658/111523542-be40d800-8731-11eb-86ab-5ec1ee6aa559.png) <br/>


6. Specify the path of the Git project you cloned in the previous steps and click finish.


7. Click on the “Gradle tasks” icon on the right of your screen <br/>
![image](https://user-images.githubusercontent.com/76922658/111503414-1f11e580-871d-11eb-8442-2bb2fd6f0ed7.png) <br/><br/>


8. Double click on the Gradle “build” task at TAB2XML > build > build. <br/>
![image](https://user-images.githubusercontent.com/76922658/111523576-cbf65d80-8731-11eb-93a8-c8dff42b2a02.png) <br/>


9. Finally, click on the run task on TAB2XML > application > run. <br/><br/><br/>



## 4. How to use TAB2XML

1.	When you run the program, you will be able to see a text field at the center of screen 
(Figure1). This is where you paste in your tablature txt file. <br/>
![image](https://user-images.githubusercontent.com/76922658/111504953-7ebcc080-871e-11eb-8ea8-e1050bb83932.png) <br/><br/>


2. To put your input, click file-open and choose your file, or copy and paste your text file in text field. <br/>


3. Once you put your text input, the system identifies errors in your input if any exists, and it notates them using a color-coded highlighting system. <br/>

There are 4 levels of highlighting:<br/>
i.	Red highlight: This is used to identify errors which may critically affect the output of the conversion.
ii.	Yellow highlight: Errors with this highlight are less critical, but we do not guarantee an accurate output with these errors.
iii.	Grey highlight: This highlight is used to identify content which may have little to no effect on the output.

Detailed below are a few examples of different error highlighting scenarios:<br/>


-	Grey highlight: “This text can’t be understood.” (Figure 2).
This is used to identify text which were not identified to be a score object (i.e measure, note, repeat instruction, e.t.c.) <br/>
![image](https://user-images.githubusercontent.com/47716543/111568520-4bf2e680-8777-11eb-9308-d6bdfbd5b9aa.png) <br/><br/>


-	Yellow highlight: “A guitar measure should have 6 lines.” (Figure 3).
As a warning, if you get such an error on a measure which seems to be accurate, make sure no text is written on the side of the measure as this makes the system identify it as two different measures. Reference the Input Requirements section of this manual for more information regarding this.<br/>
![image](https://user-images.githubusercontent.com/47716543/111569807-d3d9f000-8779-11eb-8d15-3d1d73a51154.png)
 <br/><br/>


-	Red highlight: “This annotation is either unsupported or invalid” This is used to identify  elements which are either not supported or not identified as valid measure annotations.<br/>
![image](https://user-images.githubusercontent.com/47716543/111568625-7644a400-8777-11eb-95f1-b3a37a6a11a6.png)

<br/><br/>


Note: There are more error scenarios which may occur, but they are all categorized into the three groups identified above.


4. For a more accurate output, be sure to resolve any errors highlighted red or yellow. The grey errors can usually be ignored without consequence. <br/>


How to resolve errors:
- Be sure the number of lines of instrument is right.<br/>
- Remove all unrecognizable notations in tablature and replace with dash ‘-’.<br/>
- Remove all text placed around measures, except for measure instructions (time signature and repeats)<br/>

How to resolve errors: instructional video: https://drive.google.com/file/d/174oWzswHkvnTvyask_AUpYKRjKmuz3_m/view?usp=sharing

- Be sure the number of lines of instrument is right. <br/>
- Remove all unrecognizable notations in tablature and replace with dash ‘-’. <br/>
- Remove all texts which is placed around score bars except for key notation. <br/>
- (How to fix instruction video: https://drive.google.com/file/d/174oWzswHkvnTvyask_AUpYKRjKmuz3_m/view?usp=sharing <br/>


Note: If you removed all yellow and red highlights, the score is ready to be converted. However, if there are no yellow or red highlights in the score, you can skip this step. <br/>


5. Click the “Convert” button. This opens new window for some option (Figure5). <br/>
![image](https://user-images.githubusercontent.com/76922658/111522744-f3005f80-8730-11eb-95c5-b281eef210b1.png) <br/><br/>


Title – You can set the title of song. <br/>
Artist – You can set the artist name. <br/>
File Name – You can name your file. <br/>


Note: Not all functions have been implemented. Updates will roll out gradually.


If you set all of them, click save button. <br/>


6. If you set all of them, click save button. Navigate to the location where you want to save your converted file and save it. You can also change file name (Figure 6). <br/>
![image](https://user-images.githubusercontent.com/76922658/111522873-10352e00-8731-11eb-821c-e65ee2fecd32.png) <br/><br/><br/>



## 5. Input Requirements

This program allows for the application of repeats and time signatures to individual measures. Here, we will go over the input restrictions governing these features.

For instructions in general, the following requirements are outlined:

1.	For a line to be interpreted as having instructions, it must only be composed of valid instructions separated by spaces and nothing else.
2.	The ‘tab’ button should not be used in your instruction lines as this might result in the system not applying the instructions to the correct measure.
3.	For your instructions to be recognized, the line directly below the instruction line must be a measure line or another instruction line (instruction chaining is allowed).
4.	Lines of instructions are chained by connecting the lines by one new line.
5.	The order of priority for applying instructions is left to right, up to down.







Repeats:
For repeats, the following input requirements must be followed for repeats to be correctly applied

1.	Repeats must start and end with a vertical bar, and can have any combination of spaces or dashes “-“ in-between, as seen below.<br/>
![image](https://user-images.githubusercontent.com/47716543/111568991-329e6a00-8778-11eb-81a4-1d99b5c178cc.png)<br/><br/>

2.	Repeats can be notated in any one of the three below stated ways.<br/>
![image](https://user-images.githubusercontent.com/47716543/111569288-b8bab080-8778-11eb-9ac8-e39102911eea.png)<br/><br/>

Time Signatures:
1.	The list of possible time signatures has been artificially restricted to the following generally accepted time signatures: 2/4, 2/2, 3/8, 3/4, 4/8, 4/4, 4/2, 6/8, 6/4, 9/8, 9/4, 12/8, and 12/4.
2.	If an invalid time signature is provided, the following error is received:<br/>
![image](https://user-images.githubusercontent.com/47716543/111569347-d2f48e80-8778-11eb-8bfa-4f66d2a8ee8b.png)<br/><br/>
3.	The default time signature is 4/4 if no time signature instruction is provided.
4.	Time signatures with a beat or beat count consisting of three or more values are not recognized as instructions and will make the line of instructions invalid.


Note Player:
You can also select measures and play them through our note player:<br/>
![image](https://user-images.githubusercontent.com/47716543/111569389-ec95d600-8778-11eb-8ed3-52c35a4819ff.png)<br/> ![image](https://user-images.githubusercontent.com/47716543/111569410-f7506b00-8778-11eb-9f19-b97951bcd79c.png) |<br/><br/>

5.2 Guitar

Some sample tablature text files that meet the below requirements can be found in the project folder in the directory TAB2XML/src/test/resources/test_tab_files. The tablature file input into the program must meet the following requirements:

1.	A measure must start with a vertical line after the string name.
2.	you may not have text by the side of a measure which itself is not a measure.

Note: System does not guarantee an accurate output if measure collections do not contain blank 
line dividing’s, and if they do not have clear line names specified. (i.e string names/drum names).


Note: Information of other instruments will be updated gradually.



Shield: [![CC BY-SA 4.0][cc-by-sa-shield]][cc-by-sa]

This work is licensed under a
[Creative Commons Attribution-ShareAlike 4.0 International License][cc-by-sa].

[![CC BY-SA 4.0][cc-by-sa-image]][cc-by-sa]

[cc-by-sa]: http://creativecommons.org/licenses/by-sa/4.0/
[cc-by-sa-image]: https://licensebuttons.net/l/by-sa/4.0/88x31.png
[cc-by-sa-shield]: https://img.shields.io/badge/License-CC%20BY--SA%204.0-lightgrey.svg
