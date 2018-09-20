Presentation Script Console
===========================

![Last tested in Nuix 7.6](https://img.shields.io/badge/Nuix-7.6-green.svg)

View the GitHub project [here](https://github.com/Nuix/Presentation-Script-Console) or download the latest release [here](https://github.com/Nuix/Presentation-Script-Console/releases).

# Overview

![image](https://user-images.githubusercontent.com/11775738/45847048-7a92c200-bcde-11e8-83e9-25e749e8153b.png)

**Written By:** Jason Wells

One year at the Nuix User Exchange I had a presentation in which I was going to demo writing some Ruby code and then running.  Previous experience had taught me that showing the script console on a projector can sometimes be difficult for attendees to see.  I also had some features in mind to make the experience easier for me as the presenter.  So I built this script with the following functionality:

- Easily switch between full screen window and smaller window.  Sometimes the code is the focus and should take up the entire screen, other times its in the way or less important.  The functionality allowed me to shift the focus as the situation dictated.
- Input and output text areas have a large font size.  I am of the opinion that when presenting code, the font size should be as large as reasonably possible.  Nobody wants to have to squint at a projection of a screen while trying to make sense of some code.  This tool also makes it easy to change the font size of both text areas at the same time or independently to better fit their content.
- Input and output text areas have light text on a dark background, mostly because I think this looks better for code samples :grin:
- Code input is Ruby syntax highlighted.  It is easier to make sense of code you are being shown when there is syntax highlighting.  Also it might assist in noticing the a typo when presenting.
- Code snippets can be pre-loaded into a sub directory of the script.  Snippets are then added to a menu for easy loading.
- Input and output are arranged side by side, with a divider that can be easily moved back and forth depending on which side needs the emphasis.  Buttons are provided to show only the input, only the output or show both equally.
- By default output is cleared before each script invocation, but can be switched to manual clearing in case you want to show output of different runs next to each other.

# Getting Started

## Setup

Begin by downloading the latest release of this code.  Extract the contents of the archive into your Nuix scripts directory.  In Windows the script directory is likely going to be either of the following:

- `%appdata%\Nuix\Scripts` - User level script directory
- `%programdata%\Nuix\Scripts` - System level script directory

## Building from Source

This script relies on a JAR file `PresentationScriptConsole.jar`, the source code of which lives in this repository in the `Java` sub directory.  While the compiled JAR is provided in [releases](https://github.com/Nuix/Presentation-Script-Console/releases), if you clone the repository you will want to build your own copy.

To build the JAR, load the Java source files into your IDE of choice.  Make sure the build path includes `\lib\*.jar` from the Nuix install directory.  For example, if you have Nuix 7.6 installed, include all JAR files located in `C:\Program Files\Nuix\Nuix 7.6\lib` in you build path.

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