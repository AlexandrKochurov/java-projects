public class FileStatistics {
    private final String fileName;
    private final long fileSize;
    private final int numLines;
    private final int numNonEmptyLines;
    private final int numCommentLines;


    public FileStatistics(String fileName, long fileSize, int numLines, int numNonEmptyLines, int numCommentLines) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.numLines = numLines;
        this.numNonEmptyLines = numNonEmptyLines;
        this.numCommentLines = numCommentLines;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public int getNumLines() {
        return numLines;
    }

    public int getNumNonEmptyLines() {
        return numNonEmptyLines;
    }

    public int getNumCommentLines() {
        return numCommentLines;
    }

    @Override
    public String toString() {
        return String.format("File: %s\nSize: %d bytes\nNumber of lines: %d\nNumber of non-empty lines: %d\nNumber of lines with comments: %d\n",
                fileName, fileSize, numLines, numNonEmptyLines, numCommentLines);
    }

}
