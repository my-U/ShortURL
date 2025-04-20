import json
import matplotlib.pyplot as plt
import os
import platform

# 한글 폰트 설정
if platform.system() == 'Windows':
    plt.rcParams['font.family'] = 'Malgun Gothic'
else:
    plt.rcParams['font.family'] = 'AppleGothic'
plt.rcParams['axes.unicode_minus'] = False

# 경로 설정
before_path = "k6-script/summary-before.json"
after_path = "k6-script/summary-after.json"

def load_json(path):
    with open(path, "r") as f:
        return json.load(f)

before = load_json(before_path)
after = load_json(after_path)

def extract_duration(data):
    dur = data["metrics"].get("http_req_duration", {})
    return {
        "avg": dur.get("avg", 0),
        "p90": dur.get("p(90)", 0),
        "p95": dur.get("p(95)", 0)
    }

before_dur = extract_duration(before)
after_dur = extract_duration(after)

# 시각화
labels = ['avg', 'p90', 'p95']
x = range(len(labels))

plt.figure(figsize=(8, 5))

plt.plot(x, [before_dur[k] for k in labels], marker='o', label="Before (No Redis)", color='skyblue')
plt.plot(x, [after_dur[k] for k in labels], marker='o', label="After (Redis)", color='lightgreen')

plt.xticks(x, labels)
plt.ylabel("응답 시간 (ms)")
plt.title("Redis 적용 전/후 응답 시간 비교 (avg / p90 / p95)")
plt.legend()
plt.grid(True)

plt.tight_layout()
plt.savefig("k6_response_time_p90.png")
plt.show()
