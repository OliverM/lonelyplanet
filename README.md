# lonelyplanet

An implementation of the Lonely Planet August 2014 coding test in Clojure

## Installation

To run the compiled binary:

To run under Leiningen (Clojure's default project management tool):

If Leiningen isn't installed, install it via [the instructions at leiningen.org](http://leiningen.org/).

Download from http://example.com/FIXME.

## Usage

FIXME: explanation

    $ java -jar lonelyplanet-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## Examples

...

## Bugs/Issues

If the project remit was a bit wider and included custom CSS, I'd adjust it to better display the destinations list. 
Currently All destinations are listed vertically under each other. Without the current stylesheet (e.g. using the 
browser default styling) these render properly showing the route hierarchy, but the current stylesheet removes inferior 
list item indentation.

Also I'm assuming there's only ever a single taxonomy, though the sample xml could allow for more than one (the opening 
tag is 'taxonomies'...). That taxonomy is called 'World', though, so I'm assuming all destinations are located under it.

Finally the breadcrumb navigation along the top of the article is limited to first children when showing routes, not 
all children; all children are visible in the navigation to the right (but suffer from the CSS alignment issue, above).

## License

Copyright © 2015 Oliver Mooney

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
