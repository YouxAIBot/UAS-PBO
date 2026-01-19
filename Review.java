package projekPBO.projek;

import java.time.LocalDateTime;

public class Review {
    private int id;
    private int transactionId;
    private String filmName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public Review(int id, int transactionId, String filmName, int rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.transactionId = transactionId;
        this.filmName = filmName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Review(String filmName, int rating, String comment) {
        this.filmName = filmName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public String getFilmName() {
        return filmName;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return String.format("⭐ %d - %s\n💬 %s\n📅 %s",
                rating, filmName, comment, createdAt);
    }
}
