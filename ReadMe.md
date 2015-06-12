# Patcher
Runtime patching with ASM.

This simply adds an alternative to the multiple `ClassReader`, `ClassWriter` chains I was
previously writing. Instead, register your transformers with the transformation chain and
start patching your classes at runtime!

## Prepackaged patchers
 - `ClassMerger`: This attempts to merge one 'override' class into another class.
 - `ClassReplacer`: Replace one class with another - renaming the class as you go.
 - `ClassReplacerSource`: Replace one class with a class from a subdirectory. This doesn't rename the class, but
   is useful when moving classes during the build process. 

## Usage
Add this to your `build.gradle` (or equivalent)
```groovy
repositories {
	mavenCentral()

	maven {
		name = "squiddev"
		url = "http://maven.bonzodandd.co.uk"
	}
}

dependencies {
	compile 'org.squiddev:Patcher:1.+'
}
```
