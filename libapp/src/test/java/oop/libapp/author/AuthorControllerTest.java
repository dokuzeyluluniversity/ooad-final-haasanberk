package oop.libapp.author;

import com.fasterxml.jackson.databind.ObjectMapper;
import oop.libapp.exception.APIExceptionHandler;
import oop.libapp.exception.ResourceNotFoundException;
import oop.libapp.message.ErrorMessage;
import oop.libapp.message.IErrorMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthorControllerTest {

    private MockMvc mvc;

    @Mock
    private WebApplicationContext context;

    @Mock
    private IAuthorService authorService;

    @InjectMocks
    private AuthorController authorController;

    @InjectMocks
    private APIExceptionHandler exceptionHandler;

    private JacksonTester<List<Author>> jsonAuthors;

    private JacksonTester<AuthorDto> jsonAuthorDto;

    private JacksonTester<Author> jsonAuthor;

    @Before
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());

        mvc = MockMvcBuilders.standaloneSetup(authorController).setControllerAdvice(exceptionHandler).build();

        given(context.getBean(IErrorMessage.class)).willReturn(new ErrorMessage());
    }

    @Test
    public void getEmptyAuthorsList() throws Exception {
        // Given
        given(authorService.findAll()).willReturn(new ArrayList<>());

        // When
        MockHttpServletResponse response = mvc.perform(
                get("/api/authors")
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[]");
    }

    @Test
    public void getNotEmptyAuthorsList() throws Exception {
        Author author1 = new Author("First Author", "First Author description");
        Author author2 = new Author("Second Author", "Second Author description");
        List<Author> authors = Arrays.asList(author1, author2);

        // Expected Json
        JsonContent<List<Author>> authorJsonContent = jsonAuthors.write(authors);

        // Given
        given(authorService.findAll()).willReturn(authors);

        // When
        MockHttpServletResponse response = mvc.perform(
                get("/api/authors")
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(authorJsonContent.getJson());
    }

    @Test
    public void getFilteredAuthorsList() throws Exception {
        Author author1 = new Author("John First", "John First description");
        author1.setId(1L);
        Author author2 = new Author("John Third", "John Third description");
        author2.setId(2L);
        List<Author> expectedAuthors = Arrays.asList(author1, author2);

        // Expected json
        JsonContent<List<Author>> authorJsonContent = jsonAuthors.write(expectedAuthors);

        // Given
        given(authorService.findAllByNameContaining("John")).willReturn(expectedAuthors);

        // When
        MockHttpServletResponse response = mvc.perform(
                get("/api/authors")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("name", "John")).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(authorJsonContent.getJson());
    }

    @Test
    public void getNotExistingAuthorHandlerWorks() throws Exception {
        // Given
        given(authorService.findById(1L)).willThrow(new ResourceNotFoundException("Author with this id not found"));

        // When
        MockHttpServletResponse response = mvc.perform(
                get("/api/authors/1")
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("Author with this id not found");
    }

    @Test
    public void getExistingAuthorWorks() throws Exception {
        Author author = new Author("test author", "test author description");
        author.setId(1L);

        // Expected json
        JsonContent<Author> authorJsonContent = jsonAuthor.write(author);

        // Given
        given(authorService.findById(1L)).willReturn(author);

        // When
        MockHttpServletResponse response = mvc.perform(
                get("/api/authors/1")
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(authorJsonContent.getJson());
    }

    @Test
    public void newAuthorNullValuesAreHandled() throws Exception {
        AuthorDto author = new AuthorDto(null, null);

        JsonContent<AuthorDto> authorDtoJsonContent = jsonAuthorDto.write(author);

        // When
        MockHttpServletResponse response = mvc.perform(
                post("/api/authors/")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(authorDtoJsonContent.getJson())
                        .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("name must not be null");
        assertThat(response.getContentAsString()).contains("description must not be null");
    }

    @Test
    public void newAuthorInvalidFieldLengthsAreHandled() throws Exception {
        AuthorDto author = new AuthorDto("", "desc");

        JsonContent<AuthorDto> authorDtoJsonContent = jsonAuthorDto.write(author);

        // When
        MockHttpServletResponse response = mvc.perform(
                post("/api/authors")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(authorDtoJsonContent.getJson())
                        .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("name length must be between 1 and 100");
        assertThat(response.getContentAsString()).contains("description length must be between 10 and 3000");
    }

    @Test
    public void newAuthorIsSavedCorrectly() throws Exception {
        // Sent json
        AuthorDto authorDto = new AuthorDto("test author", "test description of this author");
        JsonContent<AuthorDto> authorDtoJsonContent = jsonAuthorDto.write(authorDto);

        // After "saving"
        Author savedAuthor = new Author("test author", "test description of this author");
        savedAuthor.setId(1L);

        // Expected json
        JsonContent<Author> authorJsonContent = jsonAuthor.write(savedAuthor);

        // Given
        given(authorService.save(any(Author.class))).willReturn(savedAuthor);

        // When
        MockHttpServletResponse response = mvc.perform(
                post("/api/authors")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(authorDtoJsonContent.getJson())
                        .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(authorJsonContent.getJson());
    }

    @Test
    public void putNotExistingAuthorHandlerWorks() throws Exception {
        AuthorDto authorDto = new AuthorDto("test author", "test description of the author");
        JsonContent<AuthorDto> authorDtoJsonContent = jsonAuthorDto.write(authorDto);

        // Given
        given(authorService.findById(1L)).willThrow(new ResourceNotFoundException("Author with this id not found"));

        // When
        MockHttpServletResponse response = mvc.perform(
                put("/api/authors/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(authorDtoJsonContent.getJson())
                        .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("Author with this id not found");
    }

    @Test
    public void putAuthorNullValuesAreHandled() throws Exception {
        AuthorDto authorDto = new AuthorDto(null, null);
        JsonContent<AuthorDto> authorDtoJsonContent = jsonAuthorDto.write(authorDto);

        // When
        MockHttpServletResponse response = mvc.perform(
                put("/api/authors/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(authorDtoJsonContent.getJson())
                        .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("name must not be null");
        assertThat(response.getContentAsString()).contains("description must not be null");
    }

    @Test
    public void putAuthorInvalidFieldLengthsAreHandled() throws Exception {
        AuthorDto authorDto = new AuthorDto("", "test");
        JsonContent<AuthorDto> authorDtoJsonContent = jsonAuthorDto.write(authorDto);

        // When
        MockHttpServletResponse response = mvc.perform(
                put("/api/authors/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(authorDtoJsonContent.getJson())
                        .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("name length must be between 1 and 100");
        assertThat(response.getContentAsString()).contains("description length must be between 10 and 3000");
    }

    @Test
    public void putAuthorIsSavedCorrectly() throws Exception {
        // Sent json
        AuthorDto authorDto = new AuthorDto("test name", "test description of this author");
        JsonContent<AuthorDto> authorDtoJsonContent = jsonAuthorDto.write(authorDto);

        // Before "saving"
        Author author = new Author("initial name", "test description of this author");

        // After "saving"
        Author savedAuthor = new Author(authorDto.getName(), authorDto.getDescription());
        savedAuthor.setId(1L);

        // Expected json
        JsonContent<Author> authorJsonContent = jsonAuthor.write(savedAuthor);

        // Given
        given(authorService.findById(1L)).willReturn(author);
        given(authorService.save(author)).willReturn(savedAuthor);

        // When
        MockHttpServletResponse response = mvc.perform(
                put("/api/authors/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(authorDtoJsonContent.getJson())
                        .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(authorJsonContent.getJson());
    }

    @Test
    public void deleteAuthorDeletesExistingAuthor() throws Exception {
        // Given
        given(authorService.deleteById(1L)).willReturn(true);

        // When
        MockHttpServletResponse response = mvc.perform(
                delete("/api/authors/1")
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("{\"deleted\":true}");
    }

    @Test
    public void deleteNotExistingAuthorHandlerWorks() throws Exception {
        // Given
        given(authorService.deleteById(1L)).willThrow(new ResourceNotFoundException("Author with this id not found"));

        // When
        MockHttpServletResponse response = mvc.perform(
                delete("/api/authors/1")
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("Author with this id not found");
    }
}
