package com.zero.storage.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PagedResource<T> {

    private Page page;

    private List<T> content;

    public PagedResource(List<T> content, int size, int page, long totalElements) {
        this.page = new Page(size, page, totalElements);
        this.content = content;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class Page {
        private int size;
        private int page;
        private long totalElements;

        public boolean isHasPrevious() {
            return page > 1;
        }

        public boolean isHasNext() {
            return page < getTotalPages();
        }

        public int getTotalPages() {
            return Double.valueOf(Math.ceil(totalElements * 1.0 / size)).intValue();
        }
    }
}
