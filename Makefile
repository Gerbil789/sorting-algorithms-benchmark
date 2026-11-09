all:
	mkdir -p bin
	javac -d bin src/*.java

run: all
	java -cp bin Benchmark

clean:
	rm -rf bin