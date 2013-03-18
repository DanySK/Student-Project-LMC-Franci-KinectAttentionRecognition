Attention Tracker
--------------------------------------

Requirements:
-OpenNI v.1.5.4.0
-SensorKinect: https://github.com/avin2/SensorKinect
-Java

--------------------------------------

Run:
run the main class "AttentionTrackerApp.java"

--------------------------------------

Other:
The file AppConfig.xml contains the default parameter for the application.
To enable the visualization of the scene change the attribute of the section <visualizationParam> to "true".
Description:
-in the <attentionParam> section:
	-<maxUser> indicates the maximum number of detectable users. 
	-<tf> indicates after how many frames must be calculated the attention index.
	-<necessaryTime> indicates the time required to read a possible text on the screen.
-in the <visualizationParam> section:
	-<skeleton> indicates if it's enabled the drawing of the skelenton.
	-<rgb> indicates if it's enabled the rgb capture.
	-<printId> indicates if it's enabled the drawing of the users's ID.