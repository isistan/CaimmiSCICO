# CaimmiSCICO

This repository hosts the code of the Req2UCM tool. This is a prototype tool that enables the generation of Use Case Maps diagrams (UCMs) from natural language text.

## Setup

1. Install an Eclipse IDE. Ignore this step if you already have an existing Eclipse IDE installed. If not, please install an Eclipse from <https://www.eclipse.org/downloads/>
2. Download the project from its [GitHub page](https://github.com/isistan/CaimmiSCICO) and import it to your Eclipse IDE.
   1. Click on the **Clone or download** button on the right side of the GitHub page and download the code.
   2. In your Eclipse, click on File > Import... > General > Existing Projects into workspace, and then browse the downloaded project using the Select root directory's browse button. Next, select the project and click on Finish.
3. Install the JUCMNav plugin. In order to do this, please follow the installation instructions that can be found on <http://jucmnav.softwareengineering.ca/ucm/bin/view/ProjetSEG/DownloadingAndInstallation>

## Usage

1. Before running the project, it is possible that you may need to increase the JVM heap size. 

   1. Go to Run > Run Configurations…
   2. In the left pane of *Run Configurations* window, navigate to the *Java Application* node and select the Java application for which you need to increase the heap size. Then in the right pane, click on the *Arguments* tab.
   3. In the *VM Arguments* section, type the following arguments:
      *-Xms[CUSTOM_SIZE]* – This means that your JVM will be started with *Xms* amount of memory
      *-Xmx[CUSTOM_SIZE]* – This means that your JVM will be able to use a maximum of *Xmx* amount of memory.
      For example: **Xmx1024M** – and can use up to a maximum of 1024 MB (or 1 GB).
   4. Click on *Apply* button to save the changes.
   5. In you get stuck, you will find more information [here](http://www.planetofbits.com/eclipse/increase-jvm-heap-size-in-eclipse/) on how to do this.

2. When you run the project, a window should pop up.

3. Open a project.

   1. Go to File > Open Project and search for a .txt file containing the software requirements

   2. The .txt file must conform to the following format:

      ```
      # Commentary
      # The name of the project must be written below the @ProjectName tag
      @ProjectName
      Example UCM
      
      # The list of requirements must be written below the @Requirement tag (one requirement per line)
      @Requirement
      If a student accesses the system then the student can add a new course or drop an added course.
      After the student adds a course, the system sends the transaction information to the billing system.
      The system shall list the classes that a student can attend.
      ```

4. Once the application has loaded the project, you should click on the bottom-right buttons to run each stage of the approach. You can navigate through the top tabs to see the results of every step.

   1. Having a list of loaded requirements, click on Process (stage 1) for running the identification of responsibilities step. The duration of this step may vary depending on the number of requirements since they are all analyzed using the Stanford NLP server.
   2. When all requirements have been analyzed you will find in the tab **UCM* Responsibilities* the list of responsibilities that were found. Then, click on Process (stage 2) to run the detection of causal relationships step.
   3. You will find the available causal relationships within the tab **UCM Causal Relationships**. Now, you should click on Process (stage 3) to discover conceptual components using a clustering algorithm.
   4. Finally, on the **UCM Conceptual Components** tab you can process the last stage in order to generate the UCM diagram. After doing this, the application will inform you the location of the generated diagram (.jucm file).

5. Open the diagram using the JUCMNav plugin. To do this, just click a .jucm file under the UCM folder within the project and the pluging will open the diagram. By the time of writing this tutorial, the plugin doesn't have an auto-layout feature so it is probably that you may need to order the paths dragging the lines.

   ![alt text](https://raw.githubusercontent.com/isistan/CaimmiSCICO/master/UCMs/UCM_TEST-1.png)

## Contact

For questions please write to [req2ucm@isistan.unicen.edu.ar](mailto:req2ucm@isistan.unicen.edu.ar)