# KinetochoreDistances
ImageJ1 plugin to partly automate measuring the kinetochores in a 3 colour image 

INSTALL

    Ensure that the ImageJ version is at least 1.5 and the installation has Java 1.8.0_60 (64bit) installed. If not download the latest version of ImageJ bundled with Java and install it.

    The versions can be checked by opening ImageJ and clicking Help then About ImageJ.

    Download the latest copy of Bio-Formats into the ImageJ plugin directory.

    Place Dot_Distance.jar into the plugins directory of your ImageJ installation.

    If everything has worked Dot_Distance should be in the Plugins menu.

    Dot_Distance.java is the editable code for the plugin should improvements or changes be required.

USAGE

    You will be prompted to Open Airyscan Image. The plugin was written for 3 channel Zeiss Airyscan images with Z, its likely that any 3 colour image will work.

    When the Bio-Formats dialogue opens make sure that only split channels is ticked.

    Once the images have opened you will be prompted to select the individual colour channels.
    
    You will then be prompted to click 2 points on the image corresponding to coloured dots in the red channel (in this case kinetochores). You will be asked whether you want to do another. The plugin will end if you enter n, otherwise it will prompt for another 2 points

    Results are saved to the directory the image came from as a text file with the same name as the image.
