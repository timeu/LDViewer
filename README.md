## What is LDViewer?


LDViewer is a Google Web Toolkit (GWT) widget containing a [processingjs][0] sketch for visualizing [LD triangle plots][1].


## How do I use it?

Following steps are required:  


Either create new interface that extends or create a class that implements the base interface `ProcessingInstance` and annotate it with `@JsType`. To interact with methods on the processing sketch define methods on that interface (i.e. `testMethod`):

```JAVA
LDViewer ldviewer = new LDViewer();
ldviewer.load(new Runnable() {
   @Override
   public void run() {
       GWT.log("LDViewer loaded");
       // Interact with sketch
       ldviewer.showLDValues();
   }
});
```
To display data following information users have to call the `showLDValues()` function with following parameter:
 - position: Array of integer 
 - r2Values: multi-dimensional array of floats in a [triangular matrix][7] form.
 - start: start position (should be the first position of the position array)
 - end: end position (should be the last position of the position array)

An [example of this data][8] can be found in the sample application and one could load it this way: 
 
```JAVA
final String jsonData = GET_FROM_CLIENTBUNDLE OR AJAX CALL
LDData data = JsonUtils.safeEval(jsonData);
```

## How do I install it?

If you're using Maven, you can add the following to your `<dependencies>`
section:

```xml
    <dependency>
      <groupId>com.github.timeu.gwt-libs.ldviewer</groupId>
      <artifactId>ldviewer</artifactId>
      <version>1.0.0</version>
    </dependency>
```

LDViewer uses [GWT 2.7's][4] new [JSInterop feature][5] and thus it has to be enabled in the GWT compiler args.
For maven:
```xml
<compilerArgs>
    <compilerArg>-XjsInteropMode</compilerArg>
    <compilerArg>JS</compilerArg>
</compilerArgs>
```
or passing it to the compiler via `-XjsInteropMode`

You can also download the [jar][1] directly or check out the source using git
from <https://github.com/timeu/ldviewer.git> and build it yourself. Once
you've installed LDViewer, be sure to inherit the module in your .gwt.xml
file like this:

```xml
    <inherits name='com.github.timeu.gwtlibs.ldviewer.LDViewer'/>
```

## Where can I learn more?

 * Check out the [sample app][2] ([Source Code][3]) for a full example of using LDViewer.
 
[0]: http://processingjs.org
[1]: http://www.nature.com/nrg/journal/v4/n8/fig_tab/nrg1123_F1.html
[2]: http://search.maven.org/remotecontent?filepath=com/github/timeu/dygraphs-gwt/dygraphs-gwt/1.0.0/dygraphs-gwt-1.0.0.jar
[2]: https://timeu.github.io/processing-js-gwt
[3]: https://github.com/timeu/processing-js-gwt/tree/master/processingjs-gwt-sample
[4]: http://www.gwtproject.org/release-notes.html#Release_Notes_2_7_0_RC1
[5]: https://docs.google.com/document/d/1tir74SB-ZWrs-gQ8w-lOEV3oMY6u6lF2MmNivDEihZ4/edit#
[6]: https://github.com/timeu/LDViewer
[7]: https://en.wikipedia.org/wiki/Triangular_matrix
[8]: https://github.com/timeu/ldviewer/ldviewer-sample/src/main/resources/sample/client/data/ld_sample_data.json 