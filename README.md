# DarkBot

  This project is an attempt at a better rewrite of the Minecraft client for use in automating Minecraft that I started around early July of last year. This isn't limited to simply botting; other uses include (completely) custom clients, server monitors, and more. Multi-protocol support was recently added, allowing the same version of the bot to run on different Minecraft versions (assuming that the corresponding protocol version provider is in the protocols/ directory).

[An example task that loads WorldEdit schematic files and constructs them](http://www.youtube.com/watch?v=mahhJ6zK2BU) (written by someone else who has since lost the source)

## Getting Started

### With Eclipse
> Open Eclipse.  
> Right-click in the package explorer area and click Import...  
> Select Git > Projects from Git and click Next.  
> Select URI and click Next.  
> Paste the Git URL for DarkBot ([https://github.com/DarkStorm652/DarkBot.git](https://github.com/DarkStorm652/DarkBot.git)) and click Next, then click Next again at the Branch Selection dialog.  
> Change the directory to [your eclipse workspace]/DarkBot (change the [] to the actual path to your workspace by clicking browse) and click Next.  
> Select Use the New Project wizard and click Finish.  
> In the New Project wizard, select Java Project and click Next.  
> Type in DarkBot (exact capitalization). If you typed it in properly and all previous steps have been followed, options in the wizard should disable themselves. Click Finish if this is the case.  
> Once cloned, expand the project in your package explorer.  
> At this point you should see "src/main/java" and "src/main/protocols". If you see an empty src folder and no errors, you've missed the earlier step about changing the directory of the git clone.  
> If "src/main/resources" is not a source folder (you do not see it below the above two), then expand "src" (located below the libraries), expand "main", and right-click "resources" and click Build Path > Use as Source Folder.  
> Ensure that Eclipse automatically added all of the jars in "lib" to "Referenced Libraries". All errors should be resolved at this point.  
> Right-click build.xml and click Run As > Ant Build (the first option).  
> Wait for the build to complete and run DarkBot.jar from cmd with --help for args.  
> Hurray! The plus side to this method (though it may seem lengthier than the one below) is that you can right-click the project folder and click Team > Pull to get new updates. Make sure you run build.xml after every time you update.  

Video1: [http://www.youtube.com/watch?v=IkK3f9CJygY](http://www.youtube.com/watch?v=IkK3f9CJygY)  
Video2: [http://www.youtube.com/watch?v=AMPuucdEaOM](http://www.youtube.com/watch?v=AMPuucdEaOM)


### Without Eclipse
> Clone the project with git or download the source zip from the github page and extract it.  
> Open the folder and run build.xml (this may require cmd to execute).  
> Wait for the build to complete and run DarkBot.jar from cmd with --help for args.  
> Hurray! The downside to this is that you have to delete the folder and redo these steps every time you want to update.  

Video: [http://www.youtube.com/watch?v=8cACg\_XJsA8](http://www.youtube.com/watch?v=8cACg_XJsA8)


### To Run

Open Terminal or Command Prompt  
Switch to the directory with DarkBot.jar (`cd path/to/DarkBot` with Terminal or `cd C:\path\to\DarkBot` with Command Prompt)

For CLI Bot:
> Print arguments with `java -jar DarkBot.jar -t bot --help`  
> Use `java -jar DarkBot.jar -t bot <args>` where `<args>` consists of arguments from the list printed before (such as --username <username> or --server <server:port>)
   * If arguments keep getting printed, you are missing required arguments or have the wrong format

For CLI Spambot:
> Print arguments with `java -jar DarkBot.jar -t spambot --help`  
> Use `java -jar DarkBot.jar -t spambot <args>` where `<args>` consists of arguments from the list printed before (such as --bot-amount <count> or --server <server:port>)
   * If arguments keep getting printed, you are missing required arguments or have the wrong format

For GUI (NOT RECOMMENDED, INCOMPLETE):
> Run `java -jar DarkBot.jar -t gui --force`
