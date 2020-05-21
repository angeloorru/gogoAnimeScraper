<h1>System requirements</h1>
<ul>
<li> Java Runtime: https://www.oracle.com/technetwork/java/javase/downloads/server-jre8-downloads-2133154.html
<li> youtube-dl
<li> PhantomJS: http://phantomjs.org/download.html
</ul>

<hr>

<h2>To install youtube-dl:</h2>
Linux:
<ul>
<li>sudo apt install python-pip 
<li>pip install youtube-dl
</ul>
Mac: 
<ul>
<li>Install brew package manager: https://brew.sh/
<li>brew install youtube-dl <br>
</ul>
Windows:
<ul>
<li>Install choco in Windows: https://jcutrer.com/windows/install-chocolatey-choco-windows10
<li>Then open terminal and type: choco install -y youtube-dl ffmpeg
</ul>

<hr>

<h2>To install PhantomJs:</h2>
Linux:
<ul>
<li> The distro should come PhantomJs ready. If not: 
follow this tutorial: https://tecadmin.net/install-phantomjs-on-ubuntu/
</ul>

Mac: 
<ul>
<li> brew tap homebrew/cask
<li> brew cask install phantomjs
</ul>

Windows:
<ul>
<li> Download PhantomJs and run the .exe file (Did not try it)
</ul>

<hr>

<h2>Program Execution</h2>
<h3> The app is ready for build with all it's dependencies. To do so run: "mvn package", 
to generate the executable jar with dependencies that can be found from inside the target folder of the project.
</h3>
Assuming that Java is correctly installed in your computer (i.e path set up correctly), 
open the terminal, navigate to the location of the .jar file (i.e. \Desktop) and 
paste the following line: java -jar GoGoAnimeDownloader-3.0.jar
<br>
Follow the instructions displayed in the console. 
<br>
*Note: If the services are not available, a log file is generated with the missing episodes.


