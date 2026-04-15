export type ClassValue =
  | string
  | number
  | boolean
  | null
  | undefined
  | ClassValue[]
  | Record<string, boolean | null | undefined>;

export function cn(...inputs: ClassValue[]): string {
  const tokens: string[] = [];

  for (const input of inputs) {
    appendClassValue(tokens, input);
  }

  return tokens.join(" ");
}

function appendClassValue(tokens: string[], value: ClassValue): void {
  if (!value) {
    return;
  }

  if (typeof value === "string" || typeof value === "number") {
    tokens.push(String(value));
    return;
  }

  if (Array.isArray(value)) {
    for (const item of value) {
      appendClassValue(tokens, item);
    }
    return;
  }

  for (const [className, enabled] of Object.entries(value)) {
    if (enabled) {
      tokens.push(className);
    }
  }
}
