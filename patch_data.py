import re

with open("seats_output.sql", "r", encoding="utf-8") as f:
    generated_seats = f.read()

with open("src/main/resources/data.sql", "r", encoding="utf-8") as f:
    data_sql = f.read()

# We want to replace the sections for Zone 1, 2, 3
# Currently they start at "-- == 임영웅 콘서트 Day1 ==" and end right before "-- A석 (zone_id=4)"
start_marker = "-- == 임영웅 콘서트 Day1 ==\n"
end_marker = "\n-- A석 (zone_id=4)"

start_idx = data_sql.find(start_marker)
end_idx = data_sql.find(end_marker)

if start_idx != -1 and end_idx != -1:
    new_data_sql = data_sql[:start_idx + len(start_marker)] + "-- VIP석 (FLOOR 구역) 200석, R석 (1층 스탠드) 200석, S석 (2층 스탠드) 200석\n" + generated_seats + data_sql[end_idx:]
    
    with open("src/main/resources/data.sql", "w", encoding="utf-8") as f:
        f.write(new_data_sql)
    print("Successfully patched data.sql")
else:
    print("Could not find markers")
