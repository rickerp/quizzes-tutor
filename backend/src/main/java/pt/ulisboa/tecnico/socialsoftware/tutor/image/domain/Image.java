package pt.ulisboa.tecnico.socialsoftware.tutor.image.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.image.dto.ImageDto;

import javax.persistence.*;

@MappedSuperclass
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String url;

    @Column
    private Integer width;


    public Image() {}

    public Image(ImageDto imageDto) {
        this.url = imageDto.getUrl();
        this.width = imageDto.getWidth();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "QuestionImage{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", width=" + width +
                '}';
    }
}
