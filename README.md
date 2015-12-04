# Llanfair

[From the homepage](http://jenmaarai.com/llanfair/en/):

> Llanfair is a free software that helps speedrunners keep track of their run. Released in August 2012, its capacity for customization and its portability allowed it to garner some recognition in the scene. Developed in Java, Llanfair can run on Windows, MacOS, or Unix.

The original author Xavier "Xunkar" Sencert was kind enough to release the sources 
(see [here](https://twitter.com/Xunkar/status/671042537134624768) and [here](https://twitter.com/Xunkar/status/671099823563632641))
when I asked. I'm not completely certain if Xunkar ever intends to continue development of Llanfair himself as it
seems he uses LiveSplit now (?).

Regardless, here I will be extending the original application as best I can by adding some missing features here and 
there and fixing bugs as needed.

## Download

Check the [releases page](https://github.com/gered/Llanfair/releases) for downloadable JARs.

JARs can be run from the command line via something similar to:

```
$ java -jar Llanfair.jar
```

## Changes

### 1.5
Changes from v1.4.3 (the last official release). Note that a couple of these changes were actually by
Xunkar himself as he did some work post-1.4.3 that he just never released.

* If the JNativeHook event hook registration fails the app will display an error prompt instead of failing
  silently. Additionally on some OS's you may see your OS prompt you with some kind of accessibility permissions
  request (note that in either case, you will have to restart Llanfair for the permissions change to take effect).
* XML formats for both app config and saved runs. Some backwards compatibility for opening the old binary versions.
* Config and runs are now saved by default under `$user_home/.llanfair/`.
* Runs are now saved with a default `.lfs` file extension.
* Support for a delayed/negative run start time. Useful if you want to start the run at a time more convenient for you
  but before any of the segments should start (e.g. to skip initial loading, fadeouts, etc)
* Attempt counter (both number of total attempts and number of completed attempts).
* Additional font customization settings.
* Coloring of split time deltas using slightly different color shades based on if you're gaining/losing time while 
  already ahead/behind.
* Other very minor bug fixes.

**NOTE**: I've temporarily disabled localization support. Some of the strings used were out of sync between English
and the other languages and I ended up adding new English strings too. I only speak English and so have no way to
update the other language translations. It seemed wrong to include an option in the app to switch languages when the
other language text was "bad", so it will remain disabled until these translations are brought up to date again.

## Building and Running

You will need Gradle. Obviously any IDE with Gradle support will simply mean you can just open this project
right away in your IDE and get developing immediately. Easy.

Llanfair currently requires Java 7.

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
JAR will of course work on any OS.

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
