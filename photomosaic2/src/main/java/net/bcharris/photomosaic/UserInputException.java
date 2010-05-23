package net.bcharris.photomosaic;

public class UserInputException extends Exception {

    private final String friendlyDescription;

    public UserInputException(String friendlyDescription) {
        this.friendlyDescription = friendlyDescription;
    }

    public String getFriendlyDescription() {
        return friendlyDescription;
    }
}
