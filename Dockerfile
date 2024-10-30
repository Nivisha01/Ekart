# Use a Tomcat base image
FROM tomcat:9.0

# Expose port 8070 (the same as your application)
EXPOSE 8070

# Set the application home directory
ENV APP_HOME /usr/src/app

# Create the app directory inside the container
WORKDIR $APP_HOME

# Copy the WAR file from the local machine to the Tomcat webapps directory
COPY target/shopping-cart-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/

# Optionally, you can define the CMD if you need to run something specific
CMD ["catalina.sh", "run"]
