import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: VMtranslator <path_to_vm_file_or_directory>");
            return;
        }

        String inputPath = args[0];
        File inputFile = new File(inputPath);

        if (!inputFile.exists()) {
            System.err.println("Error: File or directory does not exist.");
            return;
        }

        try {
            if (inputFile.isFile() && inputPath.endsWith(".vm")) {
                
                String outputFilePath = inputFile.getParent() + "/" + getFileNameWithoutExtension(inputFile) + ".asm";
                translateVMFile(inputFile, new File(outputFilePath));
                System.out.println("Output created: " + outputFilePath);
            } else if (inputFile.isDirectory()) {
                
                String outputFilePath = inputFile.getAbsolutePath() + "/output.asm"; // Use a generic name
                translateVMDirectory(inputFile, new File(outputFilePath));
                System.out.println("Output created: " + outputFilePath);
            } else {
                System.err.println("Error: Input must be a .vm file or a directory containing .vm files.");
            }
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static String getFileNameWithoutExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        return lastDot == -1 ? name : name.substring(0, lastDot);
    }

    private static void translateVMFile(File vmFile, File asmFile) throws IOException {
        Parser parser = new Parser(vmFile);
        CodeWriter codeWriter = new CodeWriter(asmFile.getAbsolutePath());

        while (parser.hasMoreLines()) {
            parser.advance();
            String commandType = parser.commandType();

            
            switch (commandType) {
                case "C_ARITHMETIC":
                    codeWriter.WriteArithmetic(parser.arg1());
                    break;
                case "C_PUSH":
                case "C_POP":
                    codeWriter.WritePushPop(commandType, parser.arg1(), parser.arg2());
                    break;
                
            }
        }

        codeWriter.close(); 
    }

    private static void translateVMDirectory(File directory, File asmFile) throws IOException {
        File[] vmFiles = directory.listFiles((dir, name) -> name.endsWith(".vm"));
    
        if (vmFiles == null || vmFiles.length == 0) {
            System.err.println("Error: No .vm files found in directory.");
            return;
        }
    
        
        String directoryName = directory.getName();
        File outputFile = new File(directory.getAbsolutePath() + "/" + directoryName + ".asm");
    
        CodeWriter codeWriter = new CodeWriter(outputFile.getAbsolutePath());
    
        for (File vmFile : vmFiles) {
            Parser parser = new Parser(vmFile);
    
            while (parser.hasMoreLines()) {
                parser.advance();
                String commandType = parser.commandType();
    
                switch (commandType) {
                    case "C_ARITHMETIC":
                        codeWriter.WriteArithmetic(parser.arg1());
                        break;
                    case "C_PUSH":
                    case "C_POP":
                        codeWriter.WritePushPop(commandType, parser.arg1(), parser.arg2());
                        break;
                    
                }
            }
        }
    
        codeWriter.close(); 
    }
}