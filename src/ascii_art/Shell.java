package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * The class that is responsible for the shell commands
 */
public class Shell {
    /** commands */
    private static final String SHOW_ALL_CHARS_COMMAND = "chars";
    private static final String EXIT_COMMAND = "exit";
    private static final String SPACE_COMMAND = "space";
    private static final String ADD_COMMAND = "add";
    private static final String ALL_COMMAND = "all";
    private static final String REMOVE_COMMAND = "remove";
    private static final char RANGE_COMMAND = '-';
    private static final String RENDER_COMMAND = "render";
    private static final String RES_DOWN_COMMAND = "down";
    private static final String RESOLUTION_COMMAND = "res";
    private static final String RES_UP_COMMAND = "up";
    private static final String CONSOLE_COMMAND = "console";
    /** messages */
    private static final String ARROWS_STRING = ">>>";
    private static final String ADD_ERROR_MSG = "Did not add due incorrect format";
    private static final String REMOVE_ERR_MSG = "Did not remove due to incorrect format";
    private static final String RES_ERR_MSG = "Did not change due to exceeding boundaries";
    private static final String RES_SUCCSES_MSG = "Width set to ";
    private static final String INVALID_ERR_MSG = "Did not executed due to incorrect command";
    /** defaults variables */
    private static final String DEFAULT_FONT = "Courier New";
    private static final char SPACE_CHAR = ' ';
    private static final Character[] INIT_CHARACTERS = {'0','1','2','3','4','5','6','7','8','9'};
    private static final int FIRST_CHAR = 32;
    private static final int LAST_CHAR = 126;
    private static final int MIN_PIXELS_PER_CHAR = 2;
    private static final int INITIAL_CHARS_IN_ROW = 64;
    private static final String OUTPUT_NAMEFILE = "out.html";
    private static final int HTML_RENDERER_TYPE = 0;
    private static final int CONSOLE_RENDERER_TYPE = 1;
    private static final String WORDS_SEPARATOR = "\\s+";
    private static final int LEGAL_RES_COMMAND_SIZE = 2;
    private static final String CHARS_SEPERATOR = " ";
    private static final int LEGAL_ARR_REMOVE_COMMAND_SIZE = 2;

    private final Image img;
    private final int minCharsInRow;
    private final int maxCharsInRow;
    private AsciiOutput renderer;
    private int rendererType; // 0 means HTML 1 means console
    private int charsInRow;
    private final BrightnessImgCharMatcher brightnessImgCharMatcher;
    private char [][] curCharImg;
    private final Set<Character> charSet;

    /**
     * The constructor of the shell
     * @param img the image to be transformed to an ascii art
     */
    public Shell(Image img)
    {
        this.img = img;
        this.brightnessImgCharMatcher = new BrightnessImgCharMatcher(this.img, DEFAULT_FONT);
        this.charSet = new HashSet<>(List.of(INIT_CHARACTERS));

        this.minCharsInRow = Math.max(1, img.getWidth()/ img.getHeight());
        this.maxCharsInRow = img.getWidth()/ MIN_PIXELS_PER_CHAR;
        this.charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, this.maxCharsInRow), this.minCharsInRow);

        this.renderer = new HtmlAsciiOutput(OUTPUT_NAMEFILE,DEFAULT_FONT);
        this.rendererType = 0;


    }

    /**
     * The function that runs the main loop of the shell
     */
    public void run()
    {
        boolean exitChecker = false;

        while (!exitChecker)
        {
            System.out.print(ARROWS_STRING);
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            exitChecker = exitFinder(input);
            if (!exitChecker)
            {
                actionManager(input);
            }
        }
    }

    /**
     * The function that gets the input and sets off the logic of eache command
     * @param command the command from the input
     */
    private void actionManager(String command)
    {
        String[] commandArr = command.split(WORDS_SEPARATOR);
        String commandType = "";

        if (commandArr.length != 0)
        {
            commandType = commandArr[0];
        }

        switch (commandType){
            case SHOW_ALL_CHARS_COMMAND: {
                showAllCharsLogic(commandArr);
                break;
            }
            case ADD_COMMAND:
            {
                addRemoveLogic(commandArr,Set::add,
                        (set, chars) -> set.addAll(List.of(chars)),
                        () -> ADD_ERROR_MSG);
                break;
            }
            case REMOVE_COMMAND:
            {
                addRemoveLogic(commandArr, Set::remove,
                        (set, chars) -> List.of(chars).forEach(set::remove),
                        ()-> REMOVE_ERR_MSG);
                break;
            }
            case RESOLUTION_COMMAND:
            {
                resolutionLogic(commandArr);
                break;
            }
            case RENDER_COMMAND:
            {
                rendererLogic();
                break;
            }
            case CONSOLE_COMMAND:
            {
                consoleLogic();
                break;
            }
            default:
            {
                invalidCommand();
                break;
            }
        }
    }

    /**
     * Method that deals with the console command, it will change the output to the console.
     */
    private void consoleLogic()
    {
        if (this.rendererType == HTML_RENDERER_TYPE)
        {
            this.rendererType = CONSOLE_RENDERER_TYPE;
            this.renderer = new ConsoleAsciiOutput();
        }
    }

    /**
     * Method that deals with the render command it will create the ascii art
     */
    private void rendererLogic()
    {
        this.curCharImg = this.brightnessImgCharMatcher.chooseChars(this.charsInRow,
                this.charSet.toArray(new Character[0]));
        this.renderer.output(this.curCharImg);
    }

    /**
     * Method that deals with the resolution command, it increases or decreases the resolution
     * @param commands the command of the user as a array of strings
     */
    private void resolutionLogic(String[] commands)
    {
        if (commands.length != LEGAL_RES_COMMAND_SIZE)
        {
            invalidCommand();
            return;
        }

        String secondCommand = commands[1];

        if (secondCommand.equals(RES_UP_COMMAND)) { //contains up
            if (this.charsInRow * 2 > this.maxCharsInRow) {
                System.out.println(RES_ERR_MSG);
            }
            else {
                this.charsInRow *= 2;
                System.out.println(RES_SUCCSES_MSG + this.charsInRow);
            }
        } else if (secondCommand.equals(RES_DOWN_COMMAND)) //contains down
        {
            if (this.charsInRow / 2 < this.minCharsInRow) {
                System.out.println(RES_ERR_MSG);
            }
            else {
                this.charsInRow /= 2;
                System.out.println(RES_SUCCSES_MSG + this.charsInRow);
            }
        }
        else {
           invalidCommand();
        }
    }

    /**
     * Method that is called when the input is illegal. prints an error message
     */
    private static void invalidCommand()
    {
        System.out.println(INVALID_ERR_MSG);
    }

    /**
     * Method that deals with a logic of showing all the chars the user inserted
     * @param commandArr the user's input as a String's array
     */
    private void showAllCharsLogic(String[] commandArr)
    {
        if (commandArr.length > 1)
        {
            invalidCommand();
            return;
        }

        for (char character : this.charSet)
        {
            System.out.print(character + CHARS_SEPERATOR);
        }
        System.out.println(""); // to make a new line
    }

    /**
     * Method that deals with the add/remove logic, it's checks the input and add or remove the chars from
     * the set
     * @param commandArr the command from the user
     * @param actionForOne the function that deals with one character
     * @param actionForMany the function that deals with an array of chars
     * @param errorMessage function that returns a string for the error, depends on if it's add or remove/
     */
    private void addRemoveLogic(String[] commandArr,
                                BiConsumer<Set<Character>,Character> actionForOne,
                                BiConsumer<Set<Character>,Character[]> actionForMany,
                                Supplier<String> errorMessage)
    {
        if (commandArr.length != LEGAL_ARR_REMOVE_COMMAND_SIZE)
        {
            System.out.println(errorMessage.get());
            return;
        }
        String secondCommand = commandArr[1];

        if (secondCommand.equals(ALL_COMMAND)) {
            actionForMany.accept(this.charSet,charactersSetCreator((char) FIRST_CHAR, (char) LAST_CHAR));
        } else if (secondCommand.equals(SPACE_COMMAND)) {
            actionForOne.accept(this.charSet,SPACE_CHAR);
        } else if (secondCommand.length() == 1) {
            actionForOne.accept(this.charSet, secondCommand.charAt(0));
        } else if (secondCommand.length() == 3 && secondCommand.charAt(1) == RANGE_COMMAND) {
            char firstChar = secondCommand.charAt(0);
            char secondChar = secondCommand.charAt(2);
            //Checks if the chars are ok
            if (firstChar <= LAST_CHAR && firstChar >= FIRST_CHAR && firstChar != SPACE_CHAR &&
                    secondChar != SPACE_CHAR && secondChar <= LAST_CHAR && secondChar >= FIRST_CHAR) {
                actionForMany.accept(this.charSet,charactersSetCreator(firstChar, secondChar));
            }
            else {
                System.out.println(errorMessage.get());
            }
        }
        else {
            System.out.println(errorMessage.get());
        }
    }

    /**
     * Function that gets 2 chars and create an Array of chars with all chars between those two chars
     * @param c1 first character
     * @param c2 second character
     * @return an array of all those chars
     */
    private static Character[] charactersSetCreator(char c1, char c2)
    {
        char smallerChar = c1 <= c2 ? c1 : c2;
        char biggerChar = c1 >= c2 ? c1 : c2;

        Character[] characters = new Character[biggerChar - smallerChar + 1];

        for (int i = 0; i <= biggerChar-smallerChar; i++) {
            characters[i] = (char)(smallerChar+i);
        }

        return characters;
    }

    /**
     * Function the checks if user typed exit to finish the session
     * @param input the input from the user as a String
     * @return true if it's a legal exit command and false if it's not.
     */
    private boolean exitFinder(String input)
    {
        String [] inputArr = input.split(WORDS_SEPARATOR);
        if (inputArr.length != 1)
        {
            return false;
        }
        return inputArr[0].equals(EXIT_COMMAND);
    }
}
