package net.ed58.dlm.clients.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/11/13/013.
 */

public class PageResultList<T> {


    /**
     * list : []
     * total : 0
     */
    private int total;
    private List<T> list;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
