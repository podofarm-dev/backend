from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from bs4 import BeautifulSoup
import time
import sys

# Python 출력 인코딩 UTF-8로 설정
sys.stdout.reconfigure(encoding='utf-8')

# ChromeDriver 설정
options = Options()
options.add_argument("--lang=ko")
options.add_argument("--headless")
options.add_argument("--disable-gpu")
options.add_argument("--no-sandbox")

service = Service(r"C:\Program Files\chromedriver\chromedriver.exe")
driver = webdriver.Chrome(service=service, options=options)

base_url = "https://school.programmers.co.kr/learn/challenges?order=acceptance_desc&levels=1%2C2&languages=java&page="
page = 1
titles = []  # title만 저장할 리스트

# 크롤링 시작
while True:
    url = f"{base_url}{page}"
    driver.get(url)
    time.sleep(2)

    html = driver.page_source
    soup = BeautifulSoup(html, "html.parser", from_encoding='utf-8')  # 인코딩 UTF-8로 명시

    bookmarks = soup.select("div.bookmark a")

    if not bookmarks:
        print("No more data found. Ending scraping.")
        break

    for bookmark in bookmarks:
        title = bookmark.get_text(strip=True)
        titles.append(title)

    page += 1
    if page > 10:  # 페이지 수 제한
        break

driver.quit()

# title 출력
for title in titles:
    print(title)
