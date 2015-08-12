package ru.yandex.money.categories.events;

abstract public class BaseEvent {
    private boolean isHandled;

    private boolean isSuccess;

    private String message;

    public boolean isHandled() {
        return isHandled;
    }

    public void setHandled(boolean isHandled) {
        this.isHandled = isHandled;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
