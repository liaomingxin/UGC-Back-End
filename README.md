# UGC Back-End

This is the backend service for the UGC (User Generated Content) platform.

## Features

- Product data crawling
- Data processing
- AI content generation using OpenAI API

## Tech Stack

- Spring Boot
- MySQL
- JPA
- OpenAI API
- JSoup

## Getting Started

1. Clone the repository
2. Configure MySQL database
3. Update application.yml with your database credentials
4. Run the application

## API Endpoints

### Content Generation
POST /api/content/generate
- Generate UGC content based on product URL

### Data Processing
POST /api/data/process
- Process crawled data

### AI Generation
POST /api/ai/generate
- Generate content using AI