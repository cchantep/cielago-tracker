Cielago-tracker
===============

Tracker module for Cielago.

# Testing

## Test DB

  # ij
  ij> connect 'jdbc:derby:project/testdb;create=true';
  ij> run 'project/test/schema.sql';
  ij> run 'project/test/constr.sql';
