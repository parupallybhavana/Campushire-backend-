package com.example.campushire.config;

import com.example.campushire.entity.*;
import com.example.campushire.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PlacementDriveRepository driveRepository;

    @Autowired
    private PracticeQuestionRepository questionRepository;

    @Autowired
    private MockTestRepository mockTestRepository;

    @Autowired
    private InterviewExperienceRepository experienceRepository;

    @Autowired
    private CompanyReviewRepository reviewRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private StudyPlanTaskRepository taskRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize Default Admin
        if (!studentRepository.existsByEmail("admin@campushire.com")) {
            Student admin = new Student();
            admin.setName("System Admin");
            admin.setEmail("admin@campushire.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setBranch("Management");
            admin.setCgpa(10.0);
            admin.setGraduationYear(2024);
            admin.setSkills("Administration, System Control");
            admin.setRole(Role.ROLE_ADMIN);
            studentRepository.save(admin);
            System.out.println(">>> Created Default Admin Account: admin@campushire.com / admin123");
        }

        // Initialize Sample Student
        Student demoStudent = null;
        if (!studentRepository.existsByEmail("student@campushire.com")) {
            demoStudent = new Student();
            demoStudent.setName("Rahul Sharma");
            demoStudent.setEmail("student@campushire.com");
            demoStudent.setPassword(passwordEncoder.encode("student123"));
            demoStudent.setPhone("+91 98765 43210");
            demoStudent.setDegree("B.Tech");
            demoStudent.setBranch("Computer Science & Engineering");
            demoStudent.setCgpa(8.4);
            demoStudent.setGraduationYear(2025);
            demoStudent.setBacklogs(0);
            demoStudent.setSkills("Java, Spring Boot, React, SQL, Data Structures, Git, REST APIs, Microservices");
            demoStudent.setProjects("1. Campus Placement Portal (Full Stack Spring Boot & React)\n- Engineered automated student eligibility checker and role-based access control.\n- Implemented JWT authentication and RESTful APIs with MySQL database.\n\n2. AI Resume & Skill Analyzer (Python & NLP)\n- Developed automated keyword matching algorithm comparing job descriptions with student resumes.\n- Improved candidate matching accuracy by 35% through TF-IDF scoring.");
            demoStudent.setCertifications("• AWS Certified Cloud Practitioner\n• Oracle Certified Professional Java SE 11 Developer\n• Meta Frontend Developer Specialization (Coursera)");
            demoStudent.setRole(Role.ROLE_STUDENT);
            demoStudent.setStreakDays(5);
            demoStudent.setTotalXp(320);
            demoStudent.setSolvedCodingCount(12);
            demoStudent.setSolvedAptitudeCount(24);
            demoStudent.setCompletedMockCount(3);
            demoStudent = studentRepository.save(demoStudent);
            System.out.println(">>> Created Sample Student Account: student@campushire.com / student123");
        } else {
            demoStudent = studentRepository.findByEmail("student@campushire.com").orElse(null);
            if (demoStudent != null && (demoStudent.getProjects() == null || demoStudent.getProjects().trim().isEmpty())) {
                demoStudent.setPhone("+91 98765 43210");
                demoStudent.setDegree("B.Tech");
                demoStudent.setProjects("1. Campus Placement Portal (Full Stack Spring Boot & React)\n- Engineered automated student eligibility checker and role-based access control.\n- Implemented JWT authentication and RESTful APIs with MySQL database.\n\n2. AI Resume & Skill Analyzer (Python & NLP)\n- Developed automated keyword matching algorithm comparing job descriptions with student resumes.\n- Improved candidate matching accuracy by 35% through TF-IDF scoring.");
                demoStudent.setCertifications("• AWS Certified Cloud Practitioner\n• Oracle Certified Professional Java SE 11 Developer\n• Meta Frontend Developer Specialization (Coursera)");
                demoStudent = studentRepository.save(demoStudent);
                System.out.println(">>> Updated Sample Student Account with projects and credentials.");
            }
        }

        // Initialize Sample Companies if empty
        if (companyRepository.count() == 0) {
            Company google = new Company(null, "Google Cloud", "Leading global provider of cloud computing, AI solutions, search engine, and software tools.", "Mountain View, CA", "https://cloud.google.com");
            google.setLogoUrl("https://images.unsplash.com/photo-1573804633927-bfcbcd909acd?w=120&h=120&fit=crop&auto=format");
            google.setIndustry("Information Technology & Cloud");
            google.setHeadquarters("Mountain View, California, USA");
            google.setCompanySize("100,000+ employees");
            google.setCtcPackage("₹18 - ₹32 LPA");
            google.setSelectionProcess("1. Online Coding Round (2 Hard DSA Questions)\n2. Technical Interview I (Data Structures & System Design)\n3. Technical Interview II (Algorithms & Coding Optimization)\n4. HR & Googliness Fitment Round");
            google.setTechTopics("Data Structures (Trees, Graphs, DP), System Design, Scalable Web APIs, Multi-threading");
            google.setAptitudeTopics("Combinatorics, Probability, High-speed Quantitative Reasoning, Matrix Logic");
            google.setHrQuestions("Why Google? Describe a time you faced ambiguity. How do you handle constructive criticism?");
            google.setPrepChecklist("Master Graphs & Dynamic Programming; Practice LeetCode Hard/Medium; Understand Distributed Caching; Practice STAR responses.");

            Company microsoft = new Company(null, "Microsoft", "Global technology giant empowering every individual and organization through Azure, Windows, and AI.", "Redmond, WA", "https://microsoft.com");
            microsoft.setLogoUrl("https://images.unsplash.com/photo-1642132652859-3ef5a1048fd1?w=120&h=120&fit=crop&auto=format");
            microsoft.setIndustry("Software Development & Cloud");
            microsoft.setHeadquarters("Redmond, Washington, USA");
            microsoft.setCompanySize("150,000+ employees");
            microsoft.setCtcPackage("₹16 - ₹28 LPA");
            microsoft.setSelectionProcess("1. Online Assessment (3 Coding Questions on Codility)\n2. Technical Round 1 (Trees & Linked Lists)\n3. Technical Round 2 (System Architecture & OS Concepts)\n4. AA (As-Appropriate / HR) Round");
            microsoft.setTechTopics("Binary Trees, Graph Traversals, OOP Principles, OS Threading, SQL Optimization");
            microsoft.setAptitudeTopics("Logical Puzzles, Data Interpretation, Series Completion");
            microsoft.setHrQuestions("Tell me about yourself. What is your favorite Microsoft product and why?");
            microsoft.setPrepChecklist("Revise OS & Memory Management; Practice Tree Traversals; Prepare 2 deep project explanations.");

            Company amazon = new Company(null, "Amazon Web Services", "World-leading cloud platform and global web marketplace driven by customer obsession.", "Seattle, WA", "https://aws.amazon.com");
            amazon.setLogoUrl("https://images.unsplash.com/photo-1523474253046-8cd2748b5fd2?w=120&h=120&fit=crop&auto=format");
            amazon.setIndustry("Cloud & E-Commerce");
            amazon.setHeadquarters("Seattle, Washington, USA");
            amazon.setCompanySize("200,000+ employees");
            amazon.setCtcPackage("₹15 - ₹26 LPA");
            amazon.setSelectionProcess("1. Online Work Simulation + Coding Test\n2. Technical Round 1 (Data Structures & AWS LP)\n3. Technical Round 2 (Problem Solving & Code Design)\n4. Bar Raiser Round");
            amazon.setTechTopics("Arrays, Strings, Dynamic Programming, AWS Cloud Services, Microservices");
            amazon.setAptitudeTopics("Workplace Simulation Scenarios, Numerical Aptitude, Critical Reasoning");
            amazon.setHrQuestions("Describe a time you demonstrated Ownership. Give an example of Customer Obsession.");
            amazon.setPrepChecklist("Study Amazon's 16 Leadership Principles deeply; Map every past project to LP stories.");

            google = companyRepository.save(google);
            microsoft = companyRepository.save(microsoft);
            amazon = companyRepository.save(amazon);

            // Initialize Sample Placement Drives
            PlacementDrive drive1 = new PlacementDrive(
                    null,
                    "Software Development Engineer (SDE-1)",
                    "Build next-generation scalable cloud microservices, high-throughput backend systems, and containerized backend microservices.",
                    7.5,
                    "Java, Spring Boot, Data Structures, SQL, Microservices",
                    "Bangalore, India",
                    LocalDate.now().plusDays(30),
                    google
            );
            drive1.setSalaryCtc("₹22 LPA");
            drive1.setEligibleBranches("Computer Science, Information Technology, Electronics");
            drive1.setJobRole("SDE-1 Backend");
            drive1.setExperienceReq("Fresher / 2024-2025 Batch");
            drive1.setMinGraduationYear(2025);
            drive1.setBacklogLimit(0);
            drive1.setRecruitmentStages("Coding Round -> Tech Round 1 -> Tech Round 2 -> Googliness HR");

            PlacementDrive drive2 = new PlacementDrive(
                    null,
                    "Frontend UI/UX Engineer",
                    "Develop futuristic web applications using React.js, Tailwind CSS, TypeScript, and modern state management tools.",
                    7.0,
                    "React.js, JavaScript, HTML5, CSS3, Modern UI",
                    "Hyderabad, India",
                    LocalDate.now().plusDays(20),
                    microsoft
            );
            drive2.setSalaryCtc("₹18 LPA");
            drive2.setEligibleBranches("CSE, IT, ECE, EEE");
            drive2.setJobRole("Frontend Software Engineer");
            drive2.setExperienceReq("Fresher");
            drive2.setMinGraduationYear(2025);
            drive2.setBacklogLimit(1);
            drive2.setRecruitmentStages("Online UI Assessment -> Technical Round -> System Design -> HR");

            PlacementDrive drive3 = new PlacementDrive(
                    null,
                    "Cloud DevOps & Backend Engineer",
                    "Architect resilient AWS cloud infrastructure, Kubernetes deployments, and automated CI/CD pipelines.",
                    8.0,
                    "Java, Docker, Kubernetes, AWS, Linux, Python",
                    "Pune, India",
                    LocalDate.now().plusDays(25),
                    amazon
            );
            drive3.setSalaryCtc("₹20 LPA");
            drive3.setEligibleBranches("CSE, IT, ECE");
            drive3.setJobRole("DevOps Cloud Associate");
            drive3.setExperienceReq("Fresher");
            drive3.setMinGraduationYear(2025);
            drive3.setBacklogLimit(0);
            drive3.setRecruitmentStages("Work Simulation -> Tech Interview -> Bar Raiser");

            driveRepository.save(drive1);
            driveRepository.save(drive2);
            driveRepository.save(drive3);

            // Sample Experiences & Reviews
            if (demoStudent != null) {
                InterviewExperience exp = new InterviewExperience(
                        null,
                        demoStudent,
                        google,
                        "Google Cloud SDE-1 Interview Experience 2024",
                        "SDE-1",
                        "MEDIUM",
                        "Round 1 had 2 coding questions: 1 array sliding window and 1 binary tree LCA. Round 2 focused on Spring Boot architecture.",
                        "1. Find maximum sum subarray of size K.\n2. Lowest Common Ancestor in Binary Tree.\n3. Difference between @Component and @Service.",
                        "Practice Leetcode medium questions under 25 minutes timer. Be clear when speaking aloud during technical interviews."
                );
                experienceRepository.save(exp);

                CompanyReview review = new CompanyReview(
                        null,
                        demoStudent,
                        google,
                        5,
                        "Exceptional engineering culture and placement process!",
                        "The online assessment had clear instructions and test cases. The recruiters were extremely polite and provided helpful feedback."
                );
                reviewRepository.save(review);
            }

            System.out.println(">>> Seeded companies, placement drives, interview experiences & reviews.");
        }

        // Initialize Sample Applications if empty
        if (applicationRepository.count() == 0 && demoStudent != null) {
            List<PlacementDrive> drives = driveRepository.findAll();
            if (!drives.isEmpty()) {
                PlacementDrive firstDrive = drives.get(0);
                Application app1 = new Application(
                        null,
                        demoStudent,
                        firstDrive,
                        LocalDateTime.now().minusDays(5),
                        ApplicationStatus.SHORTLISTED
                );
                applicationRepository.save(app1);

                if (drives.size() > 1) {
                    PlacementDrive secondDrive = drives.get(1);
                    Application app2 = new Application(
                            null,
                            demoStudent,
                            secondDrive,
                            LocalDateTime.now().minusDays(2),
                            ApplicationStatus.UNDER_REVIEW
                    );
                    applicationRepository.save(app2);
                }
                System.out.println(">>> Seeded sample applications for student.");
            }
        }

        // Initialize Practice Questions if empty
        if (questionRepository.count() == 0) {
            // A. Technical Questions (C, Java, Python, DBMS, SQL, OOP, Operating Systems, Data Structures)
            PracticeQuestion t1 = new PracticeQuestion(
                    null,
                    "Pointers and Memory Allocation in C",
                    "What is the difference between malloc() and calloc() in C language, and how does free() prevent memory leaks?",
                    "TECHNICAL",
                    "INTERMEDIATE",
                    "C",
                    "void* ptr1 = malloc(10 * sizeof(int)); // uninitialized garbage\nvoid* ptr2 = calloc(10, sizeof(int)); // initialized to zero\nfree(ptr1); free(ptr2);",
                    "malloc() allocates uninitialized memory block of specified byte size. calloc() allocates multiple memory blocks and initializes all bits to zero. free() releases allocated memory back to heap."
            );
            t1.setCorrectAnswer("malloc allocates uninitialized memory; calloc allocates memory and zeroes it out.");
            questionRepository.save(t1);

            PracticeQuestion t2 = new PracticeQuestion(
                    null,
                    "Java Memory Management & JVM Architecture",
                    "Explain Heap vs Stack memory in Java. Where are object instances and local primitive variables stored?",
                    "TECHNICAL",
                    "BEGINNER",
                    "Java",
                    "// Object instance is in Heap, reference variable 'p' and local variable 'x' are in Stack frame\nPerson p = new Person(\"Alice\");\nint x = 10;",
                    "Heap memory holds all object instances and class metadata shared across threads. Stack memory stores method invocation frames, local primitive variables, and references to objects."
            );
            t2.setCorrectAnswer("Objects reside in Heap; local primitive variables and method call frames reside in Stack.");
            questionRepository.save(t2);

            PracticeQuestion t3 = new PracticeQuestion(
                    null,
                    "Python List Comprehension & Generators",
                    "What is the key performance difference between a List Comprehension and a Generator Expression in Python?",
                    "TECHNICAL",
                    "INTERMEDIATE",
                    "Python",
                    "# List comprehension (eager, allocates memory)\nnums_list = [x * 2 for x in range(1000000)]\n# Generator expression (lazy evaluation)\nnums_gen = (x * 2 for x in range(1000000))",
                    "List comprehension evaluates immediately and creates the entire list in RAM memory. Generators evaluate values lazily one-by-one upon iteration, consuming O(1) auxiliary memory."
            );
            t3.setCorrectAnswer("Generators produce items lazily on demand using O(1) memory; list comprehensions load all items into memory at once.");
            questionRepository.save(t3);

            PracticeQuestion t4 = new PracticeQuestion(
                    null,
                    "DBMS ACID Properties Explained",
                    "Explain the ACID properties of a Relational Database Management System with practical examples.",
                    "TECHNICAL",
                    "BEGINNER",
                    "DBMS",
                    "-- Transaction block ensures Atomicity & Isolation\nSTART TRANSACTION;\nUPDATE accounts SET balance = balance - 500 WHERE id = 1;\nUPDATE accounts SET balance = balance + 500 WHERE id = 2;\nCOMMIT;",
                    "Atomicity: All operations succeed or none do. Consistency: Database remains in valid state after transaction. Isolation: Concurrent transactions do not interfere with each other. Durability: Committed data survives system crashes."
            );
            t4.setCorrectAnswer("ACID stands for Atomicity, Consistency, Isolation, and Durability.");
            questionRepository.save(t4);

            PracticeQuestion t5 = new PracticeQuestion(
                    null,
                    "SQL Joins and Group By with Having",
                    "Write a query to find departments with more than 5 employees whose average salary exceeds $60,000.",
                    "TECHNICAL",
                    "INTERMEDIATE",
                    "SQL",
                    "SELECT department_id, COUNT(*) AS emp_count, AVG(salary) AS avg_sal\nFROM employees\nGROUP BY department_id\nHAVING COUNT(*) > 5 AND AVG(salary) > 60000;",
                    "HAVING clause is used to filter aggregated group results produced by GROUP BY, whereas WHERE filters individual rows prior to grouping."
            );
            t5.setCorrectAnswer("SELECT department_id FROM employees GROUP BY department_id HAVING COUNT(*) > 5 AND AVG(salary) > 60000;");
            questionRepository.save(t5);

            PracticeQuestion t6 = new PracticeQuestion(
                    null,
                    "Four Pillars of Object-Oriented Programming (OOP)",
                    "Define Abstraction, Encapsulation, Inheritance, and Polymorphism with concise definitions.",
                    "TECHNICAL",
                    "BEGINNER",
                    "OOP",
                    "// Encapsulation (private fields + getters/setters)\n// Polymorphism (Method Overriding / Overloading)\npublic class Circle extends Shape {\n    @Override public double area() { return Math.PI * r * r; }\n}",
                    "Encapsulation binds data and methods together with access protection. Abstraction hides internal implementation details. Inheritance enables code reuse and hierarchy. Polymorphism enables entities to take multiple forms at runtime/compile-time."
            );
            t6.setCorrectAnswer("Abstraction, Encapsulation, Inheritance, Polymorphism.");
            questionRepository.save(t6);

            PracticeQuestion t7 = new PracticeQuestion(
                    null,
                    "Operating System Deadlock Conditions",
                    "What are the four necessary Coffman conditions required for a deadlock to occur in an OS?",
                    "TECHNICAL",
                    "INTERMEDIATE",
                    "Operating Systems",
                    "// Prevention: Break any of the 4 conditions\n// 1. Mutual Exclusion\n// 2. Hold and Wait\n// 3. No Preemption\n// 4. Circular Wait",
                    "1. Mutual Exclusion (non-shareable resources), 2. Hold and Wait (process holds resources while requesting others), 3. No Preemption (resources cannot be forcibly confiscated), 4. Circular Wait (circular chain of process dependencies)."
            );
            t7.setCorrectAnswer("Mutual Exclusion, Hold and Wait, No Preemption, Circular Wait.");
            questionRepository.save(t7);

            PracticeQuestion t8 = new PracticeQuestion(
                    null,
                    "Data Structures: Binary Search Tree vs Hash Table",
                    "Compare time complexities of search, insertion, and deletion between a Balanced Binary Search Tree (AVL/Red-Black) and Hash Table.",
                    "TECHNICAL",
                    "INTERMEDIATE",
                    "Data Structures",
                    "// Balanced BST: Search = O(log N), Insert = O(log N), In-order traversal gives sorted order\n// Hash Table: Average Search = O(1), Worst Search = O(N) due to collisions, Unordered",
                    "Balanced BST offers guaranteed O(log N) worst-case time for search, insertion, and deletion while maintaining sorted order. Hash Table provides average O(1) lookup but lacks range ordering."
            );
            t8.setCorrectAnswer("Hash Table average O(1) vs Balanced BST guaranteed O(log N) with ordered keys.");
            questionRepository.save(t8);

            // B. Aptitude Questions (Percentages, Ratios, Time and work, Probability, Number systems, Logical reasoning)
            PracticeQuestion a1 = new PracticeQuestion(
                    null,
                    "Percentage Calculation",
                    "A student scored 72 out of 80 marks in Mathematics. What is the percentage scored by the student?",
                    "APTITUDE",
                    "BEGINNER",
                    "Percentages",
                    null,
                    "Percentage = (72 / 80) * 100 = 0.9 * 100 = 90%."
            );
            a1.setOptionsJson("[\"85%\", \"90%\", \"92%\", \"88%\"]");
            a1.setCorrectAnswer("90%");
            questionRepository.save(a1);

            PracticeQuestion a2 = new PracticeQuestion(
                    null,
                    "Ratio and Proportion",
                    "The ratio of boys to girls in a class is 5 : 3. If there are 40 students in total, how many girls are in the class?",
                    "APTITUDE",
                    "BEGINNER",
                    "Ratios",
                    null,
                    "Total ratio parts = 5 + 3 = 8 parts. 1 part = 40 / 8 = 5 students. Number of girls = 3 * 5 = 15."
            );
            a2.setOptionsJson("[\"12\", \"15\", \"20\", \"25\"]");
            a2.setCorrectAnswer("15");
            questionRepository.save(a2);

            PracticeQuestion a3 = new PracticeQuestion(
                    null,
                    "Time and Work Problem",
                    "Pipe A can fill a tank in 6 hours, and Pipe B can fill it in 12 hours. If both pipes are opened together, how long will it take to fill the tank?",
                    "APTITUDE",
                    "BEGINNER",
                    "Time and work",
                    null,
                    "Combined rate per hour = 1/6 + 1/12 = (2 + 1)/12 = 3/12 = 1/4. Total time = 4 hours."
            );
            a3.setOptionsJson("[\"3 hours\", \"4 hours\", \"4.5 hours\", \"5 hours\"]");
            a3.setCorrectAnswer("4 hours");
            questionRepository.save(a3);

            PracticeQuestion a4 = new PracticeQuestion(
                    null,
                    "Probability of Rolling Dice",
                    "Two standard 6-sided dice are rolled simultaneously. What is the probability of obtaining a sum equal to 7?",
                    "APTITUDE",
                    "INTERMEDIATE",
                    "Probability",
                    null,
                    "Total possible outcomes = 6 * 6 = 36. Pairs giving sum 7: (1,6), (2,5), (3,4), (4,3), (5,2), (6,1) -> 6 outcomes. Probability = 6/36 = 1/6."
            );
            a4.setOptionsJson("[\"1/6\", \"1/12\", \"7/36\", \"1/4\"]");
            a4.setCorrectAnswer("1/6");
            questionRepository.save(a4);

            PracticeQuestion a5 = new PracticeQuestion(
                    null,
                    "Number Systems: Divisibility Rule",
                    "What is the smallest 4-digit number that is completely divisible by 12, 15, and 18?",
                    "APTITUDE",
                    "INTERMEDIATE",
                    "Number systems",
                    null,
                    "LCM of 12, 15, and 18 is 180. Smallest 4-digit number is 1000. 1000 / 180 = 5.55. Next multiple = 180 * 6 = 1080."
            );
            a5.setOptionsJson("[\"1020\", \"1080\", \"1100\", \"1180\"]");
            a5.setCorrectAnswer("1080");
            questionRepository.save(a5);

            PracticeQuestion a6 = new PracticeQuestion(
                    null,
                    "Logical Reasoning: Number Series",
                    "Find the missing number in the sequence: 4, 9, 19, 39, 79, ?",
                    "APTITUDE",
                    "BEGINNER",
                    "Logical reasoning",
                    null,
                    "Pattern: Each term is (Previous Term * 2) + 1.\n(4*2)+1 = 9, (9*2)+1 = 19, (19*2)+1 = 39, (39*2)+1 = 79, (79*2)+1 = 159."
            );
            a6.setOptionsJson("[\"149\", \"158\", \"159\", \"169\"]");
            a6.setCorrectAnswer("159");
            questionRepository.save(a6);

            // C. HR Questions (Tell me about yourself, Strengths and weaknesses, Why should we hire you, Explain your project)
            PracticeQuestion hr1 = new PracticeQuestion(
                    null,
                    "Tell Me About Yourself",
                    "Structure a 90-second professional elevator pitch summarizing your education, core technical skills, key project achievements, and career goal.",
                    "HR",
                    "BEGINNER",
                    "Personal Background",
                    null,
                    "Framework (Present -> Past -> Future):\n1. Present: 'I am a final year Computer Science student passionate about full-stack software development.'\n2. Past: 'During my degree, I developed real-world web apps using Spring Boot, React, and MySQL, with a focus on clean REST APIs.'\n3. Future: 'I am excited about this role because your engineering team builds scalable cloud products where I can contribute and grow.'"
            );
            hr1.setHints("Keep it under 90 seconds. Focus on skills and relevance to the job role.");
            questionRepository.save(hr1);

            PracticeQuestion hr2 = new PracticeQuestion(
                    null,
                    "What Are Your Strengths and Weaknesses?",
                    "How to answer the strengths and weaknesses question authentically with examples of self-improvement.",
                    "HR",
                    "BEGINNER",
                    "Strengths and weaknesses",
                    null,
                    "Tip for Strength: Highlight measurable habits like quick debugging, problem-solving in teams, or adaptability.\nTip for Weakness: Choose a genuine, non-fatal professional weakness (e.g. initial hesitation in public speaking or over-focusing on minute details) and explain the active steps you are taking to overcome it."
            );
            questionRepository.save(hr2);

            PracticeQuestion hr3 = new PracticeQuestion(
                    null,
                    "Why Should We Hire You?",
                    "Align your academic projects, technical competence, problem-solving drive, and alignment with company culture.",
                    "HR",
                    "BEGINNER",
                    "Why should we hire you?",
                    null,
                    "Formula:\n1. Direct Skill Match: Highlight required stack (e.g. Java/React/MySQL)\n2. Fast Learner Proof: Mention projects delivered under deadlines\n3. Value Add: Commitment to writing testable, production-ready code and collaborative team spirit."
            );
            questionRepository.save(hr3);

            PracticeQuestion hr4 = new PracticeQuestion(
                    null,
                    "Explain Your Final Year / Major Project",
                    "How to explain your project using the STAR (Situation, Task, Action, Result) method in 2 minutes.",
                    "HR",
                    "BEGINNER",
                    "Explain your project",
                    null,
                    "STAR Method:\n- Situation: What problem does the project solve?\n- Task: What was your specific responsibility in the architecture/team?\n- Action: Which tech stack did you pick and why? What key algorithm or API did you write?\n- Result: Measurable outcome (e.g. reduced response time, clean responsive UI, full CRUD capabilities)."
            );
            questionRepository.save(hr4);

            System.out.println(">>> Seeded complete practice questions across Technical, Aptitude, and HR categories.");
        }

        // Initialize Mock Tests if empty
        if (mockTestRepository.count() == 0) {
            String test1Questions = "[\n" +
                    "  {\"id\":1, \"question\":\"What is the time complexity of searching an element in a balanced Binary Search Tree?\", \"options\":[\"O(1)\", \"O(log N)\", \"O(N)\", \"O(N log N)\"], \"correctAnswer\":\"O(log N)\", \"explanation\":\"A balanced BST halves the search space at each step, yielding O(log N) time.\"},\n" +
                    "  {\"id\":2, \"question\":\"Which of the following HTTP status codes represents 'Unauthorized'?\", \"options\":[\"400\", \"401\", \"403\", \"404\"], \"correctAnswer\":\"401\", \"explanation\":\"401 indicates missing or invalid authentication credentials.\"},\n" +
                    "  {\"id\":3, \"question\":\"A car travels 180 km in 3 hours. What is its speed in meters per second?\", \"options\":[\"15 m/s\", \"16.67 m/s\", \"20 m/s\", \"25 m/s\"], \"correctAnswer\":\"16.67 m/s\", \"explanation\":\"Speed = 180 km / 3 hr = 60 km/h = 60 * (5/18) = 16.67 m/s.\"},\n" +
                    "  {\"id\":4, \"question\":\"Which SQL keyword is used to eliminate duplicate rows in query results?\", \"options\":[\"UNIQUE\", \"DISTINCT\", \"GROUP\", \"FILTER\"], \"correctAnswer\":\"DISTINCT\", \"explanation\":\"SELECT DISTINCT returns only unique values.\"},\n" +
                    "  {\"id\":5, \"question\":\"If 15 workers can build a wall in 20 days, how many days will 25 workers take at the same rate?\", \"options\":[\"10 days\", \"12 days\", \"14 days\", \"16 days\"], \"correctAnswer\":\"12 days\", \"explanation\":\"Work = 15 * 20 = 300 worker-days. Days for 25 workers = 300 / 25 = 12 days.\"}\n" +
                    "]";

            MockTest test1 = new MockTest(
                    null,
                    "Campus Placement Assessment 2026 - Comprehensive Mock Test",
                    "FULL_PLACEMENT",
                    30,
                    50,
                    "Comprehensive campus placement mock test evaluating core Computer Science, SQL, Data Structures, and Quantitative Aptitude.",
                    test1Questions
            );
            mockTestRepository.save(test1);

            String test2Questions = "[\n" +
                    "  {\"id\":1, \"question\":\"In Java, which collection class does NOT permit null keys?\", \"options\":[\"HashMap\", \"TreeMap\", \"Hashtable\", \"LinkedHashMap\"], \"correctAnswer\":\"Hashtable\", \"explanation\":\"Hashtable and ConcurrentHashMap do not allow null keys or values.\"},\n" +
                    "  {\"id\":2, \"question\":\"What does JVM bytecode execute on?\", \"options\":[\"Hardware CPU directly\", \"Java Virtual Machine\", \"OS Kernel directly\", \"Browser engine\"], \"correctAnswer\":\"Java Virtual Machine\", \"explanation\":\"Java bytecode is platform-independent code interpreted/JIT-compiled by the JVM.\"},\n" +
                    "  {\"id\":3, \"question\":\"What is the worst-case time complexity of QuickSort?\", \"options\":[\"O(N)\", \"O(N log N)\", \"O(N^2)\", \"O(log N)\"], \"correctAnswer\":\"O(N^2)\", \"explanation\":\"When the pivot is consistently the smallest or largest element, QuickSort degrades to O(N^2).\"},\n" +
                    "  {\"id\":4, \"question\":\"Which normal form eliminates partial functional dependency on composite primary keys?\", \"options\":[\"1NF\", \"2NF\", \"3NF\", \"BCNF\"], \"correctAnswer\":\"2NF\", \"explanation\":\"Second Normal Form (2NF) requires 1NF and no partial dependencies.\"} \n" +
                    "]";

            MockTest test2 = new MockTest(
                    null,
                    "Core Technical & Data Structures Speed Test",
                    "TECHNICAL",
                    20,
                    40,
                    "Timed evaluation focusing on Java core principles, Data Structure complexities, SQL normalization, and algorithm analysis.",
                    test2Questions
            );
            mockTestRepository.save(test2);

            System.out.println(">>> Seeded mock tests.");
        }

        // Initialize Sample Tasks and Notifications for Demo Student
        if (demoStudent != null) {
            if (taskRepository.count() == 0) {
                taskRepository.save(new StudyPlanTask(null, demoStudent, "Arrays & Sliding Window Coding Practice", "CODING", "MONDAY", LocalDate.now(), true));
                taskRepository.save(new StudyPlanTask(null, demoStudent, "Time & Work Aptitude Drills", "APTITUDE", "TUESDAY", LocalDate.now().plusDays(1), false));
                taskRepository.save(new StudyPlanTask(null, demoStudent, "SQL Queries & Indexing Concepts", "TECHNICAL", "WEDNESDAY", LocalDate.now().plusDays(2), false));
                taskRepository.save(new StudyPlanTask(null, demoStudent, "STAR Method HR Interview Drafts", "HR", "THURSDAY", LocalDate.now().plusDays(3), false));
                taskRepository.save(new StudyPlanTask(null, demoStudent, "Full Length Placement Mock Exam", "CODING", "FRIDAY", LocalDate.now().plusDays(4), false));
            }

            if (notificationRepository.count() == 0) {
                notificationRepository.save(new Notification(null, demoStudent, "New Placement Drive Announced!", "Google Cloud has announced Software Development Engineer (SDE-1) drive. Apply before deadline.", "/student/jobs", "NEW_DRIVE"));
                notificationRepository.save(new Notification(null, demoStudent, "Study Plan Reminder", "Time to revise Time & Work aptitude questions for tomorrow's assessment.", "/student/study-plan", "REMINDER"));
                notificationRepository.save(new Notification(null, demoStudent, "Practice Milestone Unlocked!", "Congratulations! You have completed 10 practice questions and earned 50 XP.", "/student/dashboard", "ASSESSMENT"));
            }
        }
    }
}
