# mosd
## Mining Open-Source Distributions

This code was developed as part of my [master’s thesis][msc]. It can parse
the filenames out of the 100+GB of source code for all the packages in
Ubuntu, and stuff them all into a 64MB cache file in only 2.5 minutes.

[msc]: http://andrew.neitsch.ca/msc

Disclaimer: This is really hacked-together, undocumented, uncommented code.
I apologize and am working to clean it up.

I’m happy to answer any questions or provide any help: andrew@neitsch.ca

## Installation

 1. Install the xz package
 2. Run `make test`

## Creating the cache

 1. Create a symlink named `ubuntu` pointing at a folder containing a
    download of the Ubuntu archive. There should be `ubuntu/dists` and
    `ubuntu/pool` directories.
 2. Run `./run Main -w` to parse all the file names from all the source
    packages into a cache file.

## Running

  - Run `./run Main -i` to get an interactive prompt for getting basic
    statistics about packages:

      - `class <package>`, e.g., `class python3.0`, shows a summary
        classification of identifiable files in that package.
      - `unknown <package>` shows a summary of unknown file extensions in
        that package.

  - Run `./run ReloaderDriver` to get an interactive prompt. Enter the name
    of an analysis class in the `reload` package, e.g., `FilenameAnalyzer`.
    The analysis will run. Make some changes in Eclipse. Save your changes.
    Hit enter at the prompt. The updated analysis runs.

## Troubleshooting

If you get an error about `lzma` not being found, it may not be in the
`PATH` that is in effect, especially if you try to run unit tests after
launching `Eclipse.app` on the Mac.

Try launching Eclipse from the command-line instead, where it will pick up
your normal `PATH` setting.

## Citation

If you find this useful in your research, please cite it as:

    @MastersThesis{BuildSystemIssuesInMultilanguageSoftware,
      title={Build System Issues in Multilanguage Software},
      author={Andrew Neitsch},
      year = 2012,
      school = {University of Alberta},
      url = {http://hdl.handle.net/10402/era.28641},
    }
