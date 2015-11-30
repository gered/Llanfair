# Llanfair

[From the homepage](http://jenmaarai.com/llanfair/en/):

> Llanfair is a free software that helps speedrunners keep track of their run. Released in August 2012, its capacity for customization and its portability allowed it to garner some recognition in the scene. Developed in Java, Llanfair can run on Windows, MacOS, or Unix.

The original author Xavier "Xunkar" Sencert was kind enough to release the sources 
(see [here](https://twitter.com/Xunkar/status/671042537134624768) and [here](https://twitter.com/Xunkar/status/671099823563632641))
when I asked. I'm not completely certain if Xunkar ever intends to continue development of Llanfair himself as it
seems he uses LiveSplit now (?).

Regardless, here I will be extending the original application as best I can by adding some missing features here and 
there and fixing bugs as needed.

## Building and Running

You will need Gradle. The Gradle `application` plugin is being used, so you can run Llanfair simply by doing:
 
```
$ gradle run
```

To package up a JAR file for redistribution, run:

```
$ gradle shadowJar
```

Which will spit out an "uber JAR" under `build/libs` under the naming convention `llanfair-[VERSION]-all.jar`.

## TODO

Lots of stuff still to be done. :)
