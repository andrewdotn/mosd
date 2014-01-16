# mosd
## Mining Open-Source Distributions

## Installation

1. Install the libarchive and xz packages
2. Run `make` in the `jni` folder

## Troubleshoot

If you get error about `lzma` not being found, it may not be in the `PATH`
that is in effect, especially if you try to run unit tests after launching
`Eclipse.app` on the Mac.

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
