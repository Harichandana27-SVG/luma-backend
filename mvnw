#!/bin/sh
# ----------------------------------------------------------------------------
# LUMA Maven Wrapper for Unix / macOS / Linux
# ----------------------------------------------------------------------------
if command -v mvn >/dev/null 2>&1; then
    exec mvn "$@"
else
    echo "[ERROR] Maven ('mvn') is not found in your PATH."
    echo "Please install Maven or add it to PATH."
    exit 1
fi
