package com.example.kevinmeehan.todos;

/**
 * Created by kevinmeehan on 1/17/16.
 */
public class Todo {
    private int _id;
    private String _text;
    private boolean _isDeleted;
    private boolean _isDone;
    private int _order;

    public Todo(){}

    public Todo(String text, boolean isDeleted, boolean isDone) {
        super();
        this._text = text;
        this._isDeleted = isDeleted;
        this._isDone = isDone;
    }

    public int getId() {
        return _id;
    }
    public String getText() {
        return _text;
    }

    public Boolean getIsDeleted() {
        return _isDeleted;
    }

    public Boolean getIsDone() {
        return _isDone;
    }

    public Integer getOrder() {
        return _order;
    }

    public void setText(String text) {
        this._text = text;
    }

    public void setIsDeleted(int isDeleted) {
        this._isDeleted = isDeleted == 1 ? true : false;
    }

    public void setIsDone(int isDone) {
        this._isDone = isDone == 1 ? true : false;
    }

    public void setOrder(int order) { this._order = order; }
    @Override
    public String toString() {
        return _text;
        //return "Todo [id=" + _id + ", text=" + _text + ", isDeleted=" + _isDeleted + ", isDone="
        //        + _isDone + "]";
    }
}
