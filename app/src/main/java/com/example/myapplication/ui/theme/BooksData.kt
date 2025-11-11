package com.example.myapplication.data

import com.example.myapplication.R

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val price: Double,
    val rating: Float,
    val category: String,
    val description: String,
    val pages: Int,
    val language: String,
    val publisher: String,
    val publicationYear: Int,
    val isbn: String,
    val imageRes: Int
)

data class Category(
    val name: String,
    val icon: Int = android.R.drawable.ic_menu_info_details
)

object BooksData {
    val categories = listOf(
        Category("Fiction"),
        Category("Non-Fiction"),
        Category("Romance"),
        Category("Mystery"),
        Category("Sci-Fi"),
        Category("Biography")
    )

    val allBooks = listOf(
        Book(
            id = 1,
            title = "Forest Spirit",
            author = "F.BANDA",
            price = 12.99,
            rating = 4.5f,
            category = "Fiction",
            description = "A captivating story that explores the depths of human emotions and relationships in a small village. This compelling narrative takes readers on a journey through the complexities of family bonds, cultural traditions, and the pursuit of dreams.",
            pages = 320,
            language = "English",
            publisher = "African Tales Publishing",
            publicationYear = 2023,
            isbn = "978-1-234567-89-0",
            imageRes = R.drawable.forest_spirit
        ),
        Book(
            id = 2,
            title = "Free to Stay",
            author = "J.MUNTALI",
            price = 14.99,
            rating = 4.8f,
            category = "Romance",
            description = "A heartwarming romance that will make you believe in love again. Set against a beautiful backdrop, this story weaves together passion, sacrifice, and the power of true love that transcends all obstacles.",
            pages = 342,
            language = "English",
            publisher = "Romance House",
            publicationYear = 2024,
            isbn = "978-1-234567-90-6",
            imageRes = R.drawable.free_to_stay
        ),
        Book(
            id = 3,
            title = "Rose's Revenge",
            author = "George PHIRI",
            price = 13.99,
            rating = 4.7f,
            category = "Fiction",
            description = "An inspiring tale for young adults navigating the challenges of modern life. Through relatable characters and authentic dialogue, this book addresses the hopes, fears, and aspirations of today's youth.",
            pages = 285,
            language = "English",
            publisher = "Youth Voices Press",
            publicationYear = 2023,
            isbn = "978-1-234567-91-3",
            imageRes = R.drawable.roses_revenge
        ),
        Book(
            id = 4,
            title = "Survival",
            author = "JOSEPH KALUA",
            price = 11.99,
            rating = 4.6f,
            category = "Fiction",
            description = "A powerful narrative about personal development and overcoming adversity. Follow the protagonist's journey from humble beginnings to achieving their life goals through determination and resilience.",
            pages = 298,
            language = "English",
            publisher = "Progress Publishers",
            publicationYear = 2022,
            isbn = "978-1-234567-92-0",
            imageRes = R.drawable.survival
        ),
        Book(
            id = 5,
            title = "The Void",
            author = "PRINCE ALINAFE",
            price = 12.49,
            rating = 4.3f,
            category = "Non-Fiction",
            description = "An insightful exploration of what it means to live a meaningful life. Drawing from philosophy, psychology, and personal experiences, this book offers practical wisdom for finding purpose and happiness.",
            pages = 256,
            language = "English",
            publisher = "Life Lessons Press",
            publicationYear = 2023,
            isbn = "978-1-234567-93-7",
            imageRes = R.drawable.the_void
        ),
        Book(
            id = 6,
            title = "Walk Alone",
            author = "JOHN KUMWENDA",
            price = 16.99,
            rating = 4.9f,
            category = "Romance",
            description = "A touching story about the unconditional love between a mother and child. This emotional journey explores the sacrifices, joys, and challenges of motherhood while celebrating the strongest bond in human existence.",
            pages = 368,
            language = "English",
            publisher = "CLAIM MALAWI Inc",
            publicationYear = 2024,
            isbn = "978-1-234567-95-1",
            imageRes = R.drawable.walk_alone
        ),
        Book(
            id = 7,
            title = "Visions of Tomorrow",
            author = "ISAIC MFUNE",
            price = 15.99,
            rating = 4.7f,
            category = "Non-Fiction",
            description = "A guide for young adults navigating their twenties. Packed with practical advice on career, relationships, finances, and personal growth, this book is a roadmap for making the most of this crucial decade.",
            pages = 310,
            language = "English",
            publisher = "MALAWI BOOKS ASSOC.",
            publicationYear = 2023,
            isbn = "978-1-234567-96-8",
            imageRes = R.drawable.visions_of_tomorrow
        ),
        Book(
            id = 8,
            title = "Zero One",
            author = "DR EPHRAIM JOHN",
            price = 14.99,
            rating = 4.8f,
            category = "Non-Fiction",
            description = "A powerful memoir about the transformative power of education. This inspiring story follows a journey from limited opportunities to academic excellence, demonstrating that knowledge truly is liberation.",
            pages = 334,
            language = "English",
            publisher = "Educational Press",
            publicationYear = 2025,
            isbn = "978-1-234567-97-5",
            imageRes = R.drawable.zero_one
        ),
        Book(
            id = 9,
            title = "Where You Left Us",
            author = "GRACE MSISKA",
            price = 15.49,
            rating = 4.6f,
            category = "Biography",
            description = "The inspiring biography of a pioneer in Human-Computer Interaction. Learn about the challenges, breakthroughs, and vision that shaped modern technology and made computers accessible to everyone.",
            pages = 289,
            language = "English",
            publisher = "Tech Biographies",
            publicationYear = 2023,
            isbn = "978-1-234567-98-2",
            imageRes = R.drawable.where_you_left_us
        ),
        Book(
            id = 10,
            title = "Self Help",
            author = "SARAH MWALE",
            price = 13.99,
            rating = 4.6f,
            category = "Mystery",
            description = "A gripping mystery thriller that will keep you guessing until the very end. When secrets from the past resurface, a detective must unravel a web of lies to discover the truth hidden in plain sight.",
            pages = 345,
            language = "English",
            publisher = "Mystery House",
            publicationYear = 2024,
            isbn = "978-1-234567-99-9",
            imageRes = R.drawable.self_help
        ),
        Book(
            id = 11,
            title = "Zero One",
            author = "MIKE BANDA",
            price = 15.49,
            rating = 4.7f,
            category = "Sci-Fi",
            description = "An epic science fiction adventure set in the distant future. Humanity has reached the stars, but new challenges await. Explore alien worlds, advanced technology, and the eternal question of what it means to be human.",
            pages = 412,
            language = "English",
            publisher = "Future Fiction",
            publicationYear = 2024,
            isbn = "978-1-234567-00-5",
            imageRes = R.drawable.zero_one
        ),
        Book(
            id = 12,
            title = "Rising from the Ashes",
            author = "DANIEL PHIRI",
            price = 14.99,
            rating = 4.8f,
            category = "Biography",
            description = "The remarkable true story of resilience and triumph over adversity. This biography chronicles one person's journey from devastating loss to building a life of purpose and impact.",
            pages = 356,
            language = "English",
            publisher = "Life Stories Publishing",
            publicationYear = 2024,
            isbn = "978-1-234567-01-2",
            imageRes = R.drawable.mock_book
        )
    )

    fun getBookById(id: Int): Book? {
        return allBooks.find { it.id == id }
    }

    fun getBooksByCategory(category: String): List<Book> {
        return allBooks.filter { it.category == category }
    }

    fun searchBooks(query: String): List<Book> {
        return allBooks.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.author.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
    }

    fun getFeaturedBooks(): List<Book> {
        return allBooks.filter { it.rating >= 4.5f }.take(5)
    }

    fun getBestSellers(): List<Book> {
        return allBooks.sortedByDescending { it.rating }.take(4)
    }
}
