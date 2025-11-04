package cymind.dto.article;

public record CreateArticleDTO(
        String articleName, String category1, String category2,
        String category3, String content, long userId) {
}