package com.example.blog.exception;

public class DuplicateIdException extends RuntimeException {
  public DuplicateIdException(String msg) {
    super(msg);
  }
}