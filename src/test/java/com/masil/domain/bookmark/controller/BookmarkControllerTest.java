package com.masil.domain.bookmark.controller;

import com.masil.common.annotation.ControllerMockApiTest;
import com.masil.domain.bookmark.dto.BookmarkResponse;
import com.masil.domain.bookmark.dto.BookmarksElementResponse;
import com.masil.domain.bookmark.dto.BookmarksResponse;
import com.masil.domain.bookmark.service.BookmarkService;
import com.masil.domain.member.dto.response.MemberResponse;
import com.masil.domain.post.dto.PostsElementResponse;
import com.masil.domain.post.dto.PostsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({
        BookmarkController.class,
})
class BookmarkControllerTest extends ControllerMockApiTest {

    @MockBean
    private BookmarkService bookmarkService;

    private static final MemberResponse MEMBER_RESPONSE = MemberResponse.builder()
            .id(1L)
            .nickname("닉네임1")
            .build();

    private static final String AUTHORIZATION_HEADER_VALUE = "Bearer aaaaaaaa.bbbbbbbb.cccccccc";

    @Test
    @DisplayName("즐겨찾기를 성공적으로 추가한다")
    void addBookmark() throws Exception {

        // given
        given(bookmarkService.addBookmark(any(), any())).willReturn(BookmarkResponse.of(true));

        // when
        ResultActions resultActions = requestAddBookmark("/posts/1/bookmark");

        // then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andDo(document("bookmark/add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("isScrap").description("스크랩 여부")
                        )
                ));
    }

    @Test
    @DisplayName("즐겨찾기를 성공적으로 삭제한다")
    void deleteBookmark() throws Exception {

        // given
        given(bookmarkService.deleteBookmark(any(), any())).willReturn(BookmarkResponse.of(false));

        // when
        ResultActions resultActions = requestDeleteBookmark("/posts/1/bookmark");

        // then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andDo(document("bookmark/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("isScrap").description("스크랩 여부")
                        )
                ));
    }

    @Test
    @DisplayName("즐겨찾기를 성공적으로 조회한다")
    void findBookmarks() throws Exception {

        // given
        List<BookmarksElementResponse> bookmarksElementResponseList = new ArrayList<>();

        bookmarksElementResponseList.add(BookmarksElementResponse.builder()
                .postId(1L)
                .member(MEMBER_RESPONSE)
                .boardId(1L)
                .address("옥천동")
                .content("내용")
                .createDate(LocalDateTime.now())
                .modifyDate(LocalDateTime.now())
                .build());


        BookmarksResponse bookmarksResponse = new BookmarksResponse(bookmarksElementResponseList, true);
        given(bookmarkService.findBookmarks(any(), any())).willReturn(bookmarksResponse);

        // when
        ResultActions resultActions = requestFindBookmarks("/bookmarks");

        // then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andDo(document("bookmark/find",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("bookmarks.[].postId").description("게시글 id"),
                                fieldWithPath("bookmarks.[].member.id").description("작성자 id"),
                                fieldWithPath("bookmarks.[].member.nickname").description("닉네임"),
                                fieldWithPath("bookmarks.[].boardId").description("카테고리Id"),
                                fieldWithPath("bookmarks.[].address").description("주소"),
                                fieldWithPath("bookmarks.[].content").description("내용"),
                                fieldWithPath("bookmarks.[].createDate").description("생성 날짜"),
                                fieldWithPath("bookmarks.[].modifyDate").description("수정 날짜"),
                                fieldWithPath("isLast").description("마지막 페이지 여부")
                        )
                ));
    }
    private ResultActions requestAddBookmark(String url) throws Exception {
        return mockMvc.perform(post(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    private ResultActions requestDeleteBookmark(String url) throws Exception {
        return mockMvc.perform(delete(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    private ResultActions requestFindBookmarks(String url) throws Exception {
        return mockMvc.perform(get(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }
}