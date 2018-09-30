# Llanfair

**This project is not maintained anymore. I have not speedrun anything in over a year, and while I once thought I might come back to it again, that is highly unlikely to be the case anymore.**

---

[From the homepage](http://jenmaarai.com/llanfair/en/):

> Llanfair is a free software that helps speedrunners keep track of their run. Released in August 2012, its capacity for customization and its portability allowed it to garner some recognition in the scene. Developed in Java, Llanfair can run on Windows, MacOS, or Unix.

The original author Xavier "Xunkar" Sencert was kind enough to release the sources 
(see [here](https://twitter.com/Xunkar/status/671042537134624768) and [here](https://twitter.com/Xunkar/status/671099823563632641))
when I asked. Here I will be extending the original application as best I can by adding some missing features here and 
there and fixing bugs as needed.

Note that Xunkar has started working on Llanfair v2.0 which is a complete rewrite. You can 
[check it's progress here](https://github.com/xunkar/llanfair).

## Download

Check the [releases page](https://github.com/gered/Llanfair/releases) for downloadable JARs. 

Llanfair requires Java 7 or later (you are encouraged to use the most recent version of Java).

Downloaded JARs can be run from the command line via something similar to:

```
$ java -jar /path/to/Llanfair.jar
```

## Major Changes / Fixes

The main changes from v1.4.3 (the last official release from Xunkar) are as follows:

* Optional world record display, via run data from speedrun.com. Contributed by [4ilo](https://github.com/4ilo).
* Enhancements to JNativeHook support for global key events. Llanfair will prompt with an error
  if the hook could not be registered instead of failing silently. Additionally on some OS's you 
  may see your OS prompt you with some kind of accessibility permissions request.
* Choice between global or non-global hotkeys.
* Human-readable config and splits file formats (XML). This change is almost entirely based on work
  Xunkar had started after release of v1.4.3.
* Support for a delayed/negative run start time. Useful if you want to start the run at a time more convenient for you
  but before any of the segments should start (e.g. to skip initial loading, fadeouts, etc).
* "Sum of best" time display option.
* Attempt counter showing: the number of total attempts, number of completed runs and a per-session attempt counter.
* Additional font and colour customization settings.
* Coloring of split time deltas using slightly different color shades based on if you're gaining/losing time while 
  already ahead/behind.
* Run goal text setting has been changed to a more generic run sub-title setting.
* By default the config file is saved under `$user_home/.llanfair/` and the default location
  to save/load splits is `$user_home/.llanfair/splits/` (though you can of course also choose
  whatever other location you like).
* Ensure application settings are saved when a Mac user quits via Cmd+Q.
* Saved splits are now saved with a default `.lfs` file extension.
* Fix that prevents existing splits files from being accidentally overwritten when choosing "New" option from menu (after you already had a splits file open), and then choose "Save."
* User setting to control amount of files shown in the "Open Recent" menu list.
* Option to set a different default splits file directory (this is merely an additional convenience, most people probably won't use this).
* Other minor bug fixes.

### Important Note About Localization

I've temporarily disabled localization support. Some of the strings used in Llanfair were out of sync between English
and the other languages and I ended up adding new English strings too as I've been working on feature enhancements and
bug fixing. I only speak English and so have no way to update the other language translations. It seemed wrong to me to 
include an option in the app to switch languages when the other language text was incomplete, so it will remain 
disabled until these translations are brought up to date again (which will have to be done by someone else -- pull
requests are more then welcome!).
  
## Building and Running

You will need Gradle. Obviously any IDE with Gradle support will simply mean you can just open this project
right away in your IDE and get developing immediately. Easy.

Llanfair currently requires Java 7 or later.

#### Command Line Building / Running / Distribution

To build:

```
$ gradle build
```

The Gradle `application` plugin is being used, so you can run Llanfair simply by doing:
 
```
$ gradle run
```

To package up a JAR file for redistribution, run:

```
$ gradle shadowJar
```

Which will spit out an "uber JAR" under `build/libs` under the naming convention `llanfair-[VERSION]-all.jar`. This
JAR will of course work on any OS and includes all required dependencies inside it.

To build a redistributable Mac app bundle (.app):

```
$ gradle createApp
```

Which will create an app bundle under `build/macApp`.

## TODO

* Bug fixing
* Some UI cleanups, especially in the Edit Run dialog and Settings dialog.
* Even more font/color customization options?
* ...
