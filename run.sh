echo "Compiling run.txt"
java -jar ./target/lyc-compiler-1.0.0.jar ./src/main/resources/input/test.txt
if [ $? -eq 0 ]; then
    cp target/output/final.asm target/asm/final.asm
fi
