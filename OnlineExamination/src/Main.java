import java.util.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static ExamSystem examSystem = new ExamSystem();

    public static void main(String[] args) {
        examSystem.addUser(new User("puja", "@123", "pujitha", "puja@example.com"));
        examSystem.addUser(new User("sai", "@456", "sai charan", "sai@example.com"));

        while (true) {
            System.out.println("1. Login");
            System.out.println("2. Quit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    private static void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User currentUser = examSystem.validateUser(username, password);
        if (currentUser != null) {
            System.out.println("Login successful!");
            boolean quit = false;
            while (!quit) {
                System.out.println("1. Update Profile");
                System.out.println("2. Update Password");
                System.out.println("3. Start Exam");
                System.out.println("4. Logout");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        updateProfile(currentUser);
                        break;
                    case 2:
                        updatePassword(currentUser);
                        break;
                    case 3:
                        startExam(currentUser);
                        break;
                    case 4:
                        quit = true;
                        System.out.println("Logged out successfully!");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
            }
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private static void updateProfile(User user) {
        System.out.print("Enter new name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new email: ");
        String email = scanner.nextLine();
        user.updateProfile(name, email);
        System.out.println("Profile updated successfully!");
    }

    private static void updatePassword(User user) {
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        user.updatePassword(newPassword);
        System.out.println("Password updated successfully!");
    }

    private static void startExam(User user) {
        ExamSession session = new ExamSession(user, examSystem.getQuestions());
        session.start();
    }

    static class User {
        private String username;
        private String password;
        private String name;
        private String email;
        private List<String> selectedAnswers;

        public User(String username, String password, String name, String email) {
            this.username = username;
            this.password = password;
            this.name = name;
            this.email = email;
            this.selectedAnswers = new ArrayList<>();
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public void updateProfile(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public void updatePassword(String password) {
            this.password = password;
        }

        public void selectAnswer(int questionIndex, String answer) {
            if (questionIndex >= selectedAnswers.size()) {
                for (int i = selectedAnswers.size(); i <= questionIndex; i++) {
                    selectedAnswers.add("");
                }
            }
            selectedAnswers.set(questionIndex, answer);
        }

        public List<String> getSelectedAnswers() {
            return selectedAnswers;
        }
    }

    static class Question {
        private String text;
        private List<String> options;
        private int correctOptionIndex;

        public Question(String text, List<String> options, int correctOptionIndex) {
            this.text = text;
            this.options = options;
            this.correctOptionIndex = correctOptionIndex;
        }

        public String getText() {
            return text;
        }

        public List<String> getOptions() {
            return options;
        }

        public int getCorrectOptionIndex() {
            return correctOptionIndex;
        }
    }

    static class ExamSystem {
        private final Map<String, User> users;
        private final List<Question> questions;

        public ExamSystem() {
            users = new HashMap<>();
            questions = new ArrayList<>();
            loadSampleQuestions();
        }

        public void addUser(User user) {
            users.put(user.getUsername(), user);
        }

        public User validateUser(String username, String password) {
            User user = users.get(username);
            if (user != null && user.getPassword().equals(password)) {
                return user;
            }
            return null;
        }

        public List<Question> getQuestions() {
            return questions;
        }

        private void loadSampleQuestions() {
            questions.add(new Question("What is the capital of India?", Arrays.asList("Delhi", "Madrid", "Paris", "Rome"), 2));
            questions.add(new Question("Which language runs in a web browser?", Arrays.asList("Java", "C", "Python", "JavaScript"), 3));
            // Add more questions as needed
        }
    }

    static class ExamSession {
        private User user;
        private List<Question> questions;
        private Timer timer;
        private int duration; // in seconds
        private boolean isCompleted;

        public ExamSession(User user, List<Question> questions) {
            this.user = user;
            this.questions = questions;
            this.duration = 60 * 10; // Example: 10 minutes
            this.isCompleted = false;
        }

        public void start() {
            System.out.println("Exam started. You have " + (duration / 60) + " minutes.");
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    submit();
                }
            }, duration * 1000);
            takeExam();
        }

        private void takeExam() {
            Scanner scanner = new Scanner(System.in);
            for (int i = 0; i < questions.size(); i++) {
                if (isCompleted) break;
                Question question = questions.get(i);
                System.out.println((i + 1) + ". " + question.getText());
                List<String> options = question.getOptions();
                for (int j = 0; j < options.size(); j++) {
                    System.out.println((char) ('A' + j) + ". " + options.get(j));
                }
                System.out.print("Select an option (A, B, C, D): ");
                String answer = scanner.nextLine();
                user.selectAnswer(i, answer);
            }
            if (!isCompleted) {
                submit();
            }
        }

        private void submit() {
            if (!isCompleted) {
                isCompleted = true;
                timer.cancel();
                System.out.println("Exam submitted.");
                // Calculate score and show results
                int score = 0;
                List<String> selectedAnswers = user.getSelectedAnswers();
                for (int i = 0; i < questions.size(); i++) {
                    if (i < selectedAnswers.size()) {
                        Question question = questions.get(i);
                        int selectedOptionIndex = selectedAnswers.get(i).toUpperCase().charAt(0) - 'A';
                        if (selectedOptionIndex == question.getCorrectOptionIndex()) {
                            score++;
                        }
                    }
                }
                System.out.println("Your score: " + score + "/" + questions.size());
            }
        }
    }
}
