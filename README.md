Cielago-tracker
===============

Tracker module for Cielago.

# Testing

## Test DB

  # ij
  ij> connect 'jdbc:derby:project/testdb;create=true';
  ij> run 'project/test/schema.sql';
  ij> run 'project/test/constr.sql';

## Travis CI

[![Build Status](https://secure.travis-ci.org/cchantep/cielago-tracker.png?branch=develop)](http://travis-ci.org/cchantep/cielago-tracker)
