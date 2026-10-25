param(
    [string] $Repo = "Wilianjcr/HubNX",
    [string] $Keystore = "$HOME\HubNX-Keys\hubnx-release.p12"
)
$ErrorActionPreference = "Stop"
if (!(Get-Command keytool -ErrorAction SilentlyContinue)) {
    throw "Instale o Java JDK 17 e confira se keytool esta no PATH."
}
if (!(Get-Command gh -ErrorAction SilentlyContinue)) {
    throw "Instale GitHub CLI (gh) e execute gh auth login."
}
& gh auth status
if ($LASTEXITCODE -ne 0) { throw "Execute gh auth login para autenticar no GitHub." }
$parent = Split-Path -Path $Keystore -Parent
New-Item -Path $parent -ItemType Directory -Force | Out-Null
if (!(Test-Path $Keystore)) {
    Write-Host "Crie a chave definitiva. Guarde uma copia segura deste arquivo."
    & keytool -genkeypair -v -keystore $Keystore -storetype PKCS12 -keyalg RSA -keysize 3072 -validity 10000 -alias hubnx-release -dname "CN=HubNX, OU=Release, O=HubNX, L=Brasil, C=BR"
    if ($LASTEXITCODE -ne 0) { throw "Nao foi possivel gerar a chave." }
} else {
    Write-Host "Usando chave existente: $Keystore. Nao a substitua sem necessidade."
}
Write-Host "Informe a senha que protege a chave (ela nao sera publicada)."
$secure = Read-Host -AsSecureString "Senha do keystore"
$ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
try { $password = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr) }
finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr) }
if ([string]::IsNullOrWhiteSpace($password)) { throw "Senha vazia." }
try {
    $b64 = [Convert]::ToBase64String([IO.File]::ReadAllBytes($Keystore))
    $b64 | gh secret set HUBNX_SIGNING_KEYSTORE_B64 --repo $Repo
    if ($LASTEXITCODE -ne 0) { throw "Falhou o upload do keystore." }
    $password | gh secret set HUBNX_SIGNING_STORE_PASSWORD --repo $Repo
    if ($LASTEXITCODE -ne 0) { throw "Falhou a senha do keystore." }
    "hubnx-release" | gh secret set HUBNX_SIGNING_KEY_ALIAS --repo $Repo
    if ($LASTEXITCODE -ne 0) { throw "Falhou o alias." }
    $password | gh secret set HUBNX_SIGNING_KEY_PASSWORD --repo $Repo
    if ($LASTEXITCODE -ne 0) { throw "Falhou a senha da chave." }
} finally { $password = $null; $b64 = $null }
Write-Host "Secrets configurados. IMPORTANTE: crie um backup seguro de $Keystore."
Write-Host "No GitHub, execute Actions > HubNX Android Release (APK assinado) > Run workflow."
