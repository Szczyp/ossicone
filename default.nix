with import <nixpkgs> { };

stdenv.mkDerivation {
  name = "nuejure";

  buildInputs = [leiningen phantomjs nodejs];
}