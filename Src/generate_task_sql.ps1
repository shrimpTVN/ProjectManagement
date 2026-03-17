$db = Join-Path $PSScriptRoot 'src\main\resources\Database'
$projectText = Get-Content (Join-Path $db 'project.sql') -Raw
$joiningText = Get-Content (Join-Path $db 'project_joining.sql') -Raw

$projectMatches = [regex]::Matches($projectText, "\((\d+),\s*'([^']+)',\s*'([^']+)',\s*'([^']+)',\s*'([^']+)'\)")
$projects = @()
foreach ($m in $projectMatches) {
    $projects += [pscustomobject]@{
        ProId = [int]$m.Groups[1].Value
        Name = $m.Groups[2].Value
        Start = [datetime]::ParseExact($m.Groups[3].Value, 'yyyy-MM-dd HH:mm:ss', $null)
        End = [datetime]::ParseExact($m.Groups[4].Value, 'yyyy-MM-dd HH:mm:ss', $null)
    }
}
$projects = $projects | Sort-Object ProId

$joinMatches = [regex]::Matches($joiningText, "\('([^']+)',\s*(\d+),\s*(\d+),\s*(\d+)\)")
$membersByProject = @{}
foreach ($m in $joinMatches) {
    $userId = [int]$m.Groups[2].Value
    $proId = [int]$m.Groups[3].Value
    $roleId = [int]$m.Groups[4].Value
    if ($roleId -eq 3) {
        if (-not $membersByProject.ContainsKey($proId)) {
            $membersByProject[$proId] = New-Object System.Collections.ArrayList
        }
        [void]$membersByProject[$proId].Add($userId)
    }
}

$templates = @(
    @('Khao sat yeu cau', 'Thu thap va tong hop yeu cau nghiep vu cho {0}'),
    @('Phan tich quy trinh nghiep vu', 'Phan tich cac quy trinh xu ly chinh cua {0}'),
    @('Thiet ke giao dien tong quan', 'Xay dung wireframe va bo cuc tong quan cho {0}'),
    @('Thiet ke co so du lieu', 'Mo hinh hoa du lieu va bang chinh cua {0}'),
    @('Xay dung dang nhap va xac thuc', 'Phat trien chuc nang dang nhap va xac thuc nguoi dung cho {0}'),
    @('Phat trien quan ly nguoi dung', 'Tao chuc nang them sua xoa nguoi dung trong {0}'),
    @('Phat trien danh sach du lieu chinh', 'Xay dung man hinh danh sach va hien thi du lieu chinh cua {0}'),
    @('Phat trien man hinh chi tiet', 'Hoan thien giao dien va luong xu ly man hinh chi tiet cho {0}'),
    @('Tich hop tim kiem va bo loc', 'Them tim kiem va bo loc du lieu cho {0}'),
    @('Xay dung phan quyen truy cap', 'Ap dung phan quyen theo vai tro cho {0}'),
    @('Toi uu hieu nang truy van', 'Toi uu truy van va tang toc do tai du lieu cua {0}'),
    @('Viet kiem thu chuc nang', 'Xay dung test case cho cac chuc nang chinh cua {0}'),
    @('Sua loi sau kiem thu', 'Khac phuc cac loi duoc phat hien trong qua trinh test {0}'),
    @('Hoan thien tai lieu huong dan', 'Soan tai lieu huong dan su dung va van hanh cho {0}'),
    @('Nghiem thu va ban giao', 'Tong hop ket qua va chuan bi ban giao du an {0}')
)

$lines = New-Object System.Collections.Generic.List[string]
$lines.Add('INSERT INTO TASK (Task_id, Task_name, Task_description, Task_startDate, Task_endDate, Pro_id, User_id)')
$lines.Add('VALUES')

$taskId = 1
for ($p = 0; $p -lt $projects.Count; $p++) {
    $project = $projects[$p]
    $members = @($membersByProject[$project.ProId] | Sort-Object)
    if ($members.Count -lt 5) {
        throw "Project $($project.ProId) does not have 5 member assignees"
    }

    for ($i = 0; $i -lt $templates.Count; $i++) {
        $assignee = $members[$i % $members.Count]
        $taskName = $templates[$i][0]
        $taskDesc = [string]::Format($templates[$i][1], $project.Name)
        $taskStart = $project.Start.AddDays($i * 5 + 1)
        $taskEnd = $taskStart.AddDays(3)
        if ($taskEnd -gt $project.End) {
            $taskEnd = $project.End.AddDays(-1)
        }

        $suffix = if (($p -eq $projects.Count - 1) -and ($i -eq $templates.Count - 1)) { ';' } else { ',' }
        $line = "({0}, '{1}', '{2}', '{3}', '{4}', {5}, {6}){7}" -f `
            $taskId,
            $taskName.Replace("'", "''"),
            $taskDesc.Replace("'", "''"),
            $taskStart.ToString('yyyy-MM-dd HH:mm:ss'),
            $taskEnd.ToString('yyyy-MM-dd HH:mm:ss'),
            $project.ProId,
            $assignee,
            $suffix
        $lines.Add($line)
        $taskId++
    }
}

Set-Content -Path (Join-Path $db 'task.sql') -Value $lines -Encoding UTF8
Write-Host ("Generated {0} tasks for {1} projects" -f ($taskId - 1), $projects.Count)
