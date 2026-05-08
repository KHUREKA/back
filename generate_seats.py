import os

# 고려대학교 화정체육관 임영웅 콘서트 더미데이터 생성 스크립트

def generate_seats_sql(zone_id, total_rows, total_cols, start_char='A'):
    lines = []
    lines.append(f"-- Zone {zone_id} ({total_rows}x{total_cols} = {total_rows*total_cols} seats)")
    lines.append(f"INSERT INTO seats (seat_zone_id, row_label, seat_number, status, row_num, col_num, is_aisle, created_at, updated_at) VALUES")
    
    values = []
    for r in range(1, total_rows + 1):
        row_label = chr(ord(start_char) + r - 1)
        for c in range(1, total_cols + 1):
            # Aisle if it's edge column, or if it's middle columns
            is_aisle = "true" if c in [1, total_cols, total_cols//2, total_cols//2 + 1] else "false"
            val = f"({zone_id},'{row_label}','{c}','AVAILABLE',{r},{c},{is_aisle},NOW(),NOW())"
            values.append(val)
    
    # join with commas, 10 per line
    for i in range(0, len(values), 5):
        lines.append(",".join(values[i:i+5]) + ("," if i+5 < len(values) else ";"))
        
    return "\n".join(lines)

if __name__ == "__main__":
    sql_parts = []
    
    # 1. FLOOR VIP석 (zone 1): 10 rows x 20 cols = 200 seats
    sql_parts.append(generate_seats_sql(1, 10, 20, 'A'))
    
    # 2. 1층 스탠드 R석 (zone 2): 10 rows x 20 cols = 200 seats
    sql_parts.append(generate_seats_sql(2, 10, 20, 'A'))
    
    # 3. 2층 스탠드 S석 (zone 3): 10 rows x 20 cols = 200 seats
    sql_parts.append(generate_seats_sql(3, 10, 20, 'A'))
    
    with open("seats_output.sql", "w") as f:
        f.write("\n\n".join(sql_parts))
    print("Generated seats_output.sql")
