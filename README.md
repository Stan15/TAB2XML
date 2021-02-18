# TAB2XML

## About TAB2XML
TAB2XML is a tool designed to convert musical tablature in a text format into MusicXML, a popular open-source file format used for exchanging digital sheet music.  TAB2XML is currently a simple java-based application with more features being added every week.  

## System Requirements
Available Disk Space	50 MB
RAM 256 MB
Java version Java 15
Operating System	Windows, MacOS, any platform with gradle

## Installation
TAB2XML is built as a Gradle project and thus should work on any IDE of your choice. However, we will only go over how to run the program using the IntelliJ and Eclipse IDE.

###	IntelliJ
1. We first need to clone the project from the online GitHub repository onto our local device. To do that, first open IntelliJ. At the top right, select File > New > Project from Version Control <br />
![image](https://user-images.githubusercontent.com/47716543/108306574-75215680-717a-11eb-87b2-66c5555d76ab.png) <br /> <br />

2. This will direct you to a window which prompts you to enter a repository URL. Enter the link https://github.com/Stan15/TAB2XML as the URL, and select a directory where the project should be saved. <br />
![image](https://user-images.githubusercontent.com/47716543/108306635-95e9ac00-717a-11eb-870e-6ccb131f4d6b.png) <br /> <br />

3. Once the project opens, click the Gradle toolbar on the right side of your screen. <br />
![image](https://user-images.githubusercontent.com/47716543/108306702-bf0a3c80-717a-11eb-9fb5-96e6e64075e7.png)  <br /> <br />

4. When the toolbar opens, click Tasks > build, and then right click “build” and the following dialog should pop up.  Click “Run TAB2XML [build]”
 <br />
 ![image](https://user-images.githubusercontent.com/47716543/108306761-d9441a80-717a-11eb-88b6-9a8d466e8030.png)
 <br /> <br />
 5. Finally select Tasks > application, and then right click the run task and select Run ‘TAB2XML [run]’.  The program will now launch.
 
###	Eclipse
1.	Like with IntelliJ, we want to clone the project from the GitHub repository. To do this, we select File > Import. <br />
![image](https://user-images.githubusercontent.com/47716543/108306843-08f32280-717b-11eb-9137-1b68b376eec5.png)<br /><br />

2.	This will take you to a new window like the one below. Under the Git folder, click Projects from Git (with smart import) <br />
![image](https://user-images.githubusercontent.com/47716543/108306912-2b853b80-717b-11eb-9408-6b7cef69e96d.png) <br /><br />

3.	Next, click Clone URI!<br />
[image](https://user-images.githubusercontent.com/47716543/108306970-43f55600-717b-11eb-8bea-3d1d60830f2a.png)
<br /><br />

4.	This will take you to a window where you are prompted to input a URI. Paste in the link https://github.com/Stan15/TAB2XML and click proceed with the steps (clicking next) until you see the finish button. Click on that. <br />
![image](https://user-images.githubusercontent.com/47716543/108307054-69825f80-717b-11eb-8695-152f81f9f735.png)<br /><br />

5. Now we have imported the project onto our local device, we want import it again as a Gradle project. Repeat step 1. When you get to the view shown in step 2, instead of clicking projects from Git, click Existing Gradle project. Proceed till you come to this page. <br />
![image](https://user-images.githubusercontent.com/47716543/108307299-8dde3c00-717b-11eb-9a59-bf436c2db265.png)
<br /><br />

6. Specify the path of the Git project you cloned in the previous steps and click finish.
7. Click on the “Gradle tasks” icon on the right of your screen
8. Double click on the gradle “build” task at TAB2XML > build > build
![image](https://user-images.githubusercontent.com/47716543/108307371-ababa100-717b-11eb-937d-b96ca4451dee.png)<br /><br />
9.	Finally, click on the run task on TAB2XML > application > run

## Using TAB2XML
When you run our program, you will see a text field at the center of the screen. This is where you paste in your tablature text file.<br />
![image](https://user-images.githubusercontent.com/47716543/108307466-cda52380-717b-11eb-8fe6-d2f63aadf42a.png)<br /><br />
Once the tablature text file is input into the text area, click the “Convert” button. This opens a file explorer. Navigate to the location where you want to save your converted file, name your file, and click save.<br />
![image](https://user-images.githubusercontent.com/47716543/108307516-e6add480-717b-11eb-90f5-0ba967e9aad5.png)
<br /><br />

##Input Requirements
A number of sample tablature text files that meet the below requirements can be found in the project folder in the directory TAB2XML/src/test/resources/test_tab_files. The tablature file input into the program must meet the following requirements:
1.	The tablature file must start with a vertical line after the string name (e.g E | ---------|----|)
2.	Each group of measures must be separated by a blank line. e.g 
e|---12-----------12-11-9-----5-----5-----5-----5----|
B|------12----------------------0-----0-----0-----0--|
G|---------13-----13-11-9---------6-----------6------|
D|---------------------------------------------------|
A|---------------------------------------------------|
E|-0------------------------5-----------5------------|

e|---4-----4----4--5--4---2-----2----0---------------|
B|-----0-----0------------------4----0---------------|
G|-------4------4--6--4---2-----2----1---------------|
D|------------------------------4----2---------------|
A|------------------------------2----2---------------|
E|-4------------4---------2-----2----0---------------|

3.	you may not have text by the side of a measure which itself is not a measure. E.g
e|--------------------------------------------------------|
B|--------------------------------------------------------|
G|-2------------------------------------------------------|
D|-2------------------------------------------------------|x2
A|---0----------------------------------------------------|
E|--------------------------------------------------------|
The x2 by the side of the G string will affect the conversion.
4.	The line names must all be lower caps, except for the E string which can be lower caps to distinguish the lower e string from the upper E string.






 


