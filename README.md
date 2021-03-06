# lonelyplanet

An implementation of the Lonely Planet August 2014 coding test in Clojure.

## Installation

This Clojure project runs on the JVM, so a recent Java installation is required. Visit [java.com/download](https://java.com/en/download/) 
to install one if there's not one installed. Then [download the compiled binary](https://github.com/OliverM/lonelyplanet/releases/download/1.0/lonely) 
(called `lonely`), into the directory of your choice.

Compiling the binary or running the tests is best done under Leiningen (Clojure's default project management tool). 
If Leiningen isn't installed, install it via [the instructions at leiningen.org](http://leiningen.org/). Then clone the
repository into a directory of your choice. 

## Usage

To run the [compiled binary](https://github.com/OliverM/lonelyplanet/releases/download/1.0/lonely) (once Java is installed), 
at the command line type: 

    lonely input-directory output-directory [options]

The input directory should contain two files, named taxonomy.xml and destinations.xml.
These filenames can be overriden using the options below.
Options show switches (short then long), defaults, and option effects.

    -h, --help                                     Show this help message.
    -t, --taxonomy FILENAME      taxonomy.xml      Supply an alternate filename for the taxonomy.xml file.
    -d, --destinations FILENAME  destinations.xml  Supply an alternate filename for the destinations.xml file.

To run the tests (once Leiningen is installed, and the repo cloned to a local directory), in the project directory type:

    lein test

which should give you:

    lein test lonelyplanet.core-test
    lein test lonelyplanet.model-test
    lein test lonelyplanet.view-test

    Ran 9 tests containing 24 assertions.
    0 failures, 0 errors.

To generate the app from the source, at the command prompt in the project directory type: `lein bin`

Alternatively, to generate an uberjar, do the usual `lein uberjar` 

## Examples

Standard invocation supplying an input directory named input-dir and an output directory named outputdir:

    lonely inputdir outputdir

The above assumes the taxonomy file is named taxonomy.xml, and the destinations file is named destinations.xml.

The following will look in the input directory for a taxonomy file named tax.xml and a destinations file named dest.xml:

    lonely input-dir output-dir --taxonomy tax.xml --destinations --dest.xml

## Notes/Bugs/Issues

The program assumes the CSS is located at static/all.css (relative to the output directory) as per the sample files.

If the project remit was a bit wider and included custom CSS, I'd adjust it to better display the destinations list. 
Currently All destinations are listed vertically under each other. Without the current stylesheet (e.g. using the 
browser default styling) these render properly showing the route hierarchy, but the current stylesheet removes inferior 
list item indentation.

Also I'm assuming there's only ever a single taxonomy, though the sample xml could allow for more than one (the opening 
tag is 'taxonomies'...). That taxonomy is called 'World', though, so I'm assuming all destinations are located under it.

Finally the breadcrumb navigation along the top of the article is limited to first children when showing routes, not 
all children; all children are visible in the navigation to the right (but suffer from the CSS alignment issue, above).

## Performance
On a 2.4Ghz 2007-vintage Core Duo with 4GB and an SSD, mean execution of the program against the sample files takes 80ms 
(see commit history for more info). This excludes JVM startup time.

## Source code commentary
I've used [Marginalia](https://github.com/gdeer81/marginalia), a Clojure documentation tool, to generate descriptive docs from the source code comments. You [can see the commentary here](https://rawgit.com/OliverM/lonelyplanet/master/doc/uberdoc.html).

## License

Copyright © 2015 Oliver Mooney

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
