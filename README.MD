Presentation Script Console
===========================

![This script was last tested in Nuix 7.6](https://img.shields.io/badge/Script%20Tested%20in%20Nuix-7.6-green.svg)

View the GitHub project [here](https://github.com/Nuix/Presentation-Script-Console) or download the latest release [here](https://github.com/Nuix/Presentation-Script-Console/releases).

# Overview

![image](https://user-images.githubusercontent.com/11775738/45847048-7a92c200-bcde-11e8-83e9-25e749e8153b.png)

**Written By:** Jason Wells

One year at the Nuix User Exchange I had a presentation in which I was going to demo writing some Ruby code and then running.  Previous experience had taught me that showing the script console on a projector can sometimes be difficult for attendees to see.  I also had some features in mind to make the experience easier for me as the presenter.  So I built this script with the following functionality:

- Easily switch between full screen window and smaller window
- Input and output text areas have a large font size
- Font size can be changed for input and/or output
- Input and output text areas have light text on a dark background
- Code input is Ruby syntax highlighted
- Ruby files placed in script sub directory will be listed in snippets menu for easy loading
- Input and output are arranged side by side separated by a divider that can be moved.  Buttons are provided to show only the input, only the output or both equally.
- By default output is cleared before each script invocation, but can be switched to manual

# Getting Started

## Setup

Begin by downloading the latest release of this code.  Extract the contents of the archive into your Nuix scripts directory.  In Windows the script directory is likely going to be either of the following:

- `%appdata%\Nuix\Scripts` - User level script directory
- `%programdata%\Nuix\Scripts` - System level script directory

## Building from Source

This script relies on a JAR file `PresentationScriptConsole.jar`, the source code of which lives in this repository in the `Java` sub directory.  While the compiled JAR is provided in [releases](https://github.com/Nuix/Presentation-Script-Console/releases), if you clone the repository you will want to build your own copy.

To build the JAR, load the Java source files into your IDE of choice.  Make sure the build path includes `\lib\*.jar` from the Nuix install directory.  For example, if you have Nuix 7.6 installed, include all JAR files on your build path which are located in:

`C:\Program Files\Nuix\Nuix 7.6\lib`

Note that the JAR is intended to be built against JRE 1.8.

# License

```
Copyright 2018 Nuix

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```