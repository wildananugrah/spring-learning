package com.user.account.app;

import com.user.account.app.config.DataConfig;
import com.user.account.app.entity.Author;
import com.user.account.app.entity.Book;
import com.user.account.app.repository.AuthorRepository;
import com.user.account.app.service.DatabaseResetService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final DataConfig dataConfig;
    private final DatabaseResetService resetService;

    public DataInitializer(AuthorRepository authorRepository, DataConfig dataConfig, DatabaseResetService resetService) {
        this.authorRepository = authorRepository;
        this.dataConfig = dataConfig;
        this.resetService = resetService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Clear existing data and reset sequences
        resetService.truncateAndReset();

        System.out.println("\n🔄 Initializing sample data...");

        // You can change this in application.properties: app.data.number-of-authors
        // Small dataset: 5-10 authors (good for quick tests)
        // Medium dataset: 50-100 authors (noticeable difference)
        // Large dataset: 500-1000 authors (MASSIVE difference)
        int numberOfAuthors = dataConfig.getNumberOfAuthors();

        long startTime = System.currentTimeMillis();

        // Category data for variety
        String[] firstNames = {"James", "Joshua", "Robert", "Martin", "Eric", "Kent", "Gang",
                              "Erich", "Richard", "Ralph", "John", "Donald", "Andrew", "Brian",
                              "Bjarne", "Dennis", "Ken", "Linus", "Tim", "Larry", "Sergey",
                              "Mark", "Jeff", "Bill", "Steve", "Ada", "Grace", "Margaret",
                              "Barbara", "Frances", "Jean", "Katherine", "Dorothy", "Marlyn"};

        String[] lastNames = {"Gosling", "Bloch", "Martin", "Fowler", "Evans", "Beck", "Gamma",
                             "Freeman", "Helm", "Johnson", "Vlissides", "Knuth", "Hunt", "Thomas",
                             "Stroustrup", "Ritchie", "Thompson", "Torvalds", "Berners-Lee", "Page",
                             "Brin", "Zuckerberg", "Bezos", "Gates", "Jobs", "Lovelace", "Hopper",
                             "Hamilton", "Liskov", "Allen", "Bartik", "Johnson", "Vaughan", "Wescoff"};

        String[] bookTitles = {"Programming", "Software Engineering", "Design Patterns",
                              "Clean Code", "Refactoring", "Architecture", "Algorithms",
                              "Data Structures", "Database Design", "Web Development",
                              "Machine Learning", "Artificial Intelligence", "Cloud Computing",
                              "Microservices", "DevOps", "Agile Development", "Testing",
                              "Security", "Performance Optimization", "Scalability"};

        String[] subjects = {"Java", "Python", "JavaScript", "C++", "Go", "Rust", "Kotlin",
                            "Swift", "Ruby", "PHP", "TypeScript", "Scala", "Clojure", "Haskell"};

        int totalBooks = 0;

        for (int i = 1; i <= numberOfAuthors; i++) {
            // Create author with varied names
            String firstName = firstNames[i % firstNames.length];
            String lastName = lastNames[(i * 7) % lastNames.length];
            String authorName = firstName + " " + lastName + " " + i;
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + i + "@tech.com";

            Author author = new Author(authorName, email);

            // Each author has 2-5 books (varied)
            int numBooks = 2 + (i % 4);
            totalBooks += numBooks;

            for (int j = 1; j <= numBooks; j++) {
                String subject = subjects[(i + j) % subjects.length];
                String title = bookTitles[(i * j) % bookTitles.length];
                String bookTitle = subject + " " + title + " Vol." + j;
                String isbn = String.format("ISBN-%03d-%03d", i, j);

                author.addBook(new Book(bookTitle, isbn));
            }

            // Save in batches for better performance
            authorRepository.save(author);

            // Progress indicator for large datasets
            if (i % 50 == 0) {
                System.out.println("  📚 Created " + i + " authors so far...");
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("\n✅ Sample data initialized!");
        System.out.println("   📊 Statistics:");
        System.out.println("   - Authors: " + numberOfAuthors);
        System.out.println("   - Books: " + totalBooks);
        System.out.println("   - Initialization time: " + duration + "ms");
        System.out.println("\n🎯 Expected N+1 queries: " + (numberOfAuthors + 1) + " queries");
        System.out.println("🎯 Expected FETCH JOIN queries: 1 query");
        System.out.println("📈 Performance difference will be " + (numberOfAuthors * 10) + "x or more!\n");
    }
}
