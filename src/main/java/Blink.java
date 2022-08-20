import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.ArrayList;

public class Blink {

    private static final String SPACING = "--------------------------------------";
    private ArrayList<Task> store;
    private boolean endSession = false;
    private final String path;

    public Blink(String path)  {
        this.path = path;
        this.store = new ArrayList<>();
        try {
            readSaveFile();
        } catch (FileNotFoundException e) {
            System.out.println("No save file detected");
        } catch (BlinkException e) {
            System.out.println(e.getMessage());
        }
    }

    enum Command {
        BYE, DEADLINE, DELETE, EVENT, LIST, MARK, TODO, UNMARK
    }

    private void readSaveFile() throws FileNotFoundException, BlinkException {
        File file = new File(path);
        Scanner sc = new Scanner(file);
        while (sc.hasNext()) {
            String line = sc.nextLine();
            if (line.isBlank()) {
                continue;
            }
            String[] info = line.split("\\|", 3);
            switch(info[0].strip()) {
                case "T":
                    if (info[1].strip().equals("0")) {
                        this.store.add(new ToDos(info[2].strip()));
                    } else {
                        ToDos temp = new ToDos(info[2].strip());
                        temp.mark();
                        this.store.add(temp);
                    }
                    break;
                case "D":
                    if (info[1].strip().equals("0")) {
                        String[] temp = info[2].split("\\|");
                        this.store.add(new Deadlines(temp[0].strip(), temp[1].strip()));
                    } else {
                        String[] temp = info[2].split("\\|");
                        Deadlines anoDeadline = new Deadlines(temp[0].strip(), temp[1].strip());
                        anoDeadline.mark();
                        this.store.add(anoDeadline);
                    }
                    break;
                case "E":
                    if (info[1].strip().equals("0")) {
                        String[] temp = info[2].split("\\|");
                        this.store.add(new Events(temp[0].strip(), temp[1].strip()));
                    } else {
                        String[] temp = info[2].split("\\|");
                        Events anoDeadline = new Events(temp[0].strip(), temp[1].strip());
                        anoDeadline.mark();
                        this.store.add(anoDeadline);
                    }
                    break;
            }
        }
        sc.close();
    }

    private void writeSaveFile() {
        try {
            new File(this.path).getParentFile().mkdirs();
            FileWriter fw = new FileWriter(this.path);
            for (int x = 0; x < this.store.size(); x++) {
                Task temp = this.store.get(x);
                fw.write(temp.saveString());
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Saving error: " + e.getMessage());
        }
    }

    private void welcome() {
        System.out.println(Blink.SPACING + "\n" +
                "Hello! Blink here\n" +
                "What can I do for you today?\n" +
                Blink.SPACING);
    }

    private void goodbye() {
        System.out.println("Bye bye~ Glad to be of service :D");
    }

    private void listTask() {
        System.out.println("Here are the tasks in your list:");
        for(int x = 0; x < this.store.size(); x++) {
            System.out.println(x+1 + ". " + this.store.get(x));
        }
    }

    private void unMarkTask(String[] input) throws BlinkException {
        if (input.length == 2) {
            int pos = Integer.parseInt(input[1]);
            if (pos < 1 || pos > this.store.size()) {
                throw new BlinkException("Number input does not exist");
            }
            System.out.println(this.store.get(pos - 1).unMark());
        } else {
            throw new BlinkException("There is an error in command inputted");
        }
        writeSaveFile();
    }

    private void markTask(String[] input) throws BlinkException {
        if (input.length == 2) {
            int pos = Integer.parseInt(input[1]);
            if (pos < 1 || pos > this.store.size()) {
                throw new BlinkException("Number input does not exist");
            }
            System.out.println(this.store.get(pos - 1).mark());
        } else {
            throw new BlinkException("There is an error in command inputted");
        }
        writeSaveFile();
    }

    private void deleteTask(String[] input) throws BlinkException {
        if (input.length == 2) {
            int pos = Integer.parseInt(input[1]);
            if (pos < 1 || pos > this.store.size()) {
                throw new BlinkException("Number input does not exist");
            }
            Task temp = this.store.get(pos - 1);
            this.store.remove(pos - 1);
            System.out.println("Task have been removed successfully\n" +
                    temp + "\nNow you have " + this.store.size() + " task remaining");
        } else {
            throw new BlinkException("There is an error in command inputted");
        }
        writeSaveFile();
    }

    private void addToDos(String[] input) throws BlinkException {
        if (input.length != 2) {
            throw new BlinkException("☹ OOPS!!! Missing description of todo.");
        }
        ToDos temp = new ToDos(input[1].strip());
        this.store.add(temp);
        System.out.println("Alright, this task has been successfully added!\n" +
                temp +
                "\nTotal of " + this.store.size() + " tasks now");
        writeSaveFile();
    }

    private void addDeadlines(String[] input) throws BlinkException {
        if (input.length != 2) {
            throw new BlinkException("☹ OOPS!!! Missing description of deadline.");
        }
        String[] sep = input[1].split("/by");
        if (sep.length != 2) {
            throw new BlinkException("Error in command for deadline");
        }
        Deadlines temp = new Deadlines(sep[0].strip(), sep[1].strip());
        this.store.add(temp);
        System.out.println("Alright, this task has been successfully added!\n" +
                temp +
                "\nTotal of " + this.store.size() + " tasks now");
        writeSaveFile();
    }

    private void addEvents(String[] input) throws BlinkException {
        if (input.length != 2) {
            throw new BlinkException("☹ OOPS!!! Missing description of event.");
        }
        String[] sep = input[1].split("/at");
        if (sep.length != 2) {
            throw new BlinkException("Error in command for event");
        }
        Events temp = new Events(sep[0].strip(), sep[1].strip());
        this.store.add(temp);
        System.out.println("Alright, this task has been successfully added!\n" +
                temp +
                "\nTotal of " + this.store.size() + " tasks now");
        writeSaveFile();
    }

    private void start(Scanner sc) {
        this.welcome();

        while(sc.hasNext()) {
            try {
                String[] input = sc.nextLine().strip().split(" ", 2);
                if (input[0].isEmpty()) {
                    continue;
                }
                Command command = Command.valueOf(input[0].strip().toUpperCase());
                switch(command) {
                    case BYE:
                        this.goodbye();
                        this.endSession = true;
                        break;
                    case LIST:
                        this.listTask();
                        break;
                    case UNMARK:
                        this.unMarkTask(input);
                        break;
                    case MARK:
                        this.markTask(input);
                        break;
                    case EVENT:
                        this.addEvents(input);
                        break;
                    case DEADLINE:
                        this.addDeadlines(input);
                        break;
                    case TODO:
                        this.addToDos(input);
                        break;
                    case DELETE:
                        this.deleteTask(input);
                        break;
                }
            } catch (BlinkException e) {
                System.out.println(e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Number is expected");
            } catch (IllegalArgumentException e) {
                System.out.println("Unknown command input");
            }
            System.out.println(Blink.SPACING);
            if (this.endSession) {
                break;
            }
        }
    }
    public static void main(String[] args) {
        String path = System.getProperty("user.home") + "/Blink/blink.txt";
        Scanner scanner = new Scanner(System.in);
        Blink blink = new Blink(path);
        blink.start(scanner);
    }
}

