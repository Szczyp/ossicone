with import <nixpkgs> { };

stdenv.mkDerivation {
  name = "ossicone";

  buildInputs = [jdk];
}