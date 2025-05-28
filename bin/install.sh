#!/bin/bash
set -e

TOOL_NAME="pwmanager"
JAR_NAME="pwmanager-1.0.jar"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "Welcome to the $TOOL_NAME installer"
echo

read -p "Install globally? (requires sudo - n for local) [Y/n]: " installPath
if [[ "$installPath" == "n" || "$installPath" == "N" ]]; then
    BIN_DIR="$HOME/.local/bin/$TOOL_NAME"
else
    BIN_DIR="/usr/local/$TOOL_NAME"
fi

# Create the install directory
if [[ ! -d "$BIN_DIR" ]]; then
    if [[ "$BIN_DIR" == /usr/local/* ]]; then
        sudo mkdir -p "$BIN_DIR"
    else
        mkdir -p "$BIN_DIR"
    fi
fi

# Copy JAR to install directory
if [[ "$BIN_DIR" == /usr/local/* ]]; then
    sudo cp "$SCRIPT_DIR/$JAR_NAME" "$BIN_DIR/"
else
    cp "$SCRIPT_DIR/$JAR_NAME" "$BIN_DIR/"
fi

# Create launcher script
LAUNCHER="$BIN_DIR/$TOOL_NAME"
cat <<EOF | (if [[ "$BIN_DIR" == /usr/local/* ]]; then sudo tee "$LAUNCHER" > /dev/null; else tee "$LAUNCHER" > /dev/null; fi)
#!/bin/bash
DIR=\$(cd "\$(dirname "\$0")" && pwd)
exec java -jar "\$DIR/$JAR_NAME" "\$@"
EOF

if [[ "$BIN_DIR" == /usr/local/* ]]; then
    sudo chmod +x "$LAUNCHER"
else
    chmod +x "$LAUNCHER"
fi

echo "Installed at: $BIN_DIR"
echo

read -p "Do you want to add \"$BIN_DIR\" to your PATH? [Y/n]: " addToPath
if [[ ! "$addToPath" =~ ^[nN]$ ]]; then
    SHELL_RC=""
    if [[ -n "$ZSH_VERSION" ]]; then
        SHELL_RC="$HOME/.zshrc"
    elif [[ -n "$BASH_VERSION" ]]; then
        SHELL_RC="$HOME/.bashrc"
    else
        SHELL_RC="$HOME/.profile"
    fi
    echo "export PATH=\"$BIN_DIR:\$PATH\"" >> "$SHELL_RC"
    echo "Added $BIN_DIR to your PATH in $SHELL_RC. Restart your terminal or run:"
    echo "  export PATH=\"$BIN_DIR:\$PATH\""
else
    echo "Skipping PATH update."
fi

echo
echo "Make sure \"$BIN_DIR\" is in your PATH to run \"$TOOL_NAME\" from anywhere."