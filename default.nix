with import <nixpkgs> { };

stdenv.mkDerivation {
  name = "ossicone";

  buildInputs = [leiningen phantomjs nodejs];
}